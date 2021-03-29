//
//  ViewController.swift
//  TwittaSave
//
//  Created by emmanuel.kehinde on 11/02/2018.
//  Copyright © 2018 emmanuel.kehinde. All rights reserved.
//

import UIKit
import AVKit
import Firebase
import shared

class MainViewController: UIViewController, UIDocumentInteractionControllerDelegate {
    
    @IBOutlet weak var tweetTextField: RoundedTextField!
    @IBOutlet weak var filenameTextField: RoundedTextField!
    @IBOutlet weak var downloadButton: RoundedButton!
    
    private var spinnerView: UIView?

    private var progressView:  UIProgressView = {
        UIProgressView(progressViewStyle: UIProgressViewStyle.default)
    }()

    private lazy var progressContainerView: UIView = {
        let containerView = UIView.init(frame: view.bounds)
        if #available(iOS 13.0, *) {
            containerView.backgroundColor = UIColor.systemBackground.withAlphaComponent(0.8)
        } else {
            containerView.backgroundColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 0.9)
        }
        return containerView
    }()

    private var downloadManager: DownloadManager?
    private var tweetId: String?
    private var lastCopiedTweetLink: String = ""

    override func viewDidLoad() {
        super.viewDidLoad()

        //To hide keyboard on return pressed
        tweetTextField.delegate = self
        filenameTextField.delegate = self

        if FeaturesController.shared.isClipboardDetectionEnabled {
            checkAndDisplayClipboardDialog()
        }

        NotificationCenter.default.addObserver(
            self,
            selector: #selector(willEnterForeground),
            name: Notification.Name.UIApplicationWillEnterForeground,
            object: nil
        )
    }

    @objc private func willEnterForeground() {
        if FeaturesController.shared.isClipboardDetectionEnabled {
            checkAndDisplayClipboardDialog()
        }
    }
    
    private func checkAndDisplayClipboardDialog() {
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) { [weak self] in
            if UserDefaults.standard.isClipboardCheckDisabled { return }
            self?.checkClipboardForTwitterLink()
        }
    }
    
    private func checkClipboardForTwitterLink() {
        let pasteboard = UIPasteboard.general
        if let clipboardString = pasteboard.string {
            if clipboardString == lastCopiedTweetLink { return }
            if !isValidTweetUrl(clipboardString) { return }
            
            let title = lastCopiedTweetLink.isEmpty ? "Tweet link found in Clipboard" : "New tweet link found in Clipboard"
            
            self.lastCopiedTweetLink = clipboardString
            showPasteFromClipboardDialog(title, clipboardString)
        }
    }
    
    private func showPasteFromClipboardDialog(_ title: String, _ clipboardString: String) {
        let ac = UIAlertController(
            title: title,
            message: "Will you like to paste the last copied tweet link?",
            preferredStyle: .alert
        )
        ac.addAction(UIAlertAction(title: "Yes", style: .default, handler: { [weak self] _ in
            self?.tweetTextField.text = clipboardString
        }))
        ac.addAction(UIAlertAction(title: "No", style: .default, handler: { _ in
            ac.dismiss(animated: true, completion: nil)
            self.showClipboardCheckDialog()
        }))
        present(ac, animated: true)
    }
    
    private func showClipboardCheckDialog() {
        let ac = UIAlertController(
            title: "Disable Clipboard Check",
            message: "Will you like to permanently stop TwittaSave from checking your clipboard for tweets?",
            preferredStyle: .alert
        )
        ac.addAction(UIAlertAction(title: "Yes", style: .default, handler: { _ in
            UserDefaults.standard.isClipboardCheckDisabled = true
        }))
        ac.addAction(UIAlertAction(title: "No", style: .default))
        present(ac, animated: true)
    }

    @IBAction func downloadBtnPressed(_ sender: Any) {
        guard let tweetUrl = tweetTextField.text, !tweetUrl.isEmpty else {
            self.tweetTextField.wiggle()
            return
        }
        lastCopiedTweetLink = tweetUrl
        showSpinner()

        let commonDIModule = CommonIosDIModule(twitterCredentialsProvider: TwitterCredentialsDataProvider.shared)
        let twitterClient = commonDIModule.twitterClient
        twitterClient.getMediaData(tweetUrl: tweetUrl) { mediaData in
            self.proceedWithDownload(mediaData: mediaData)
        } onError: { throwable in
            self.hideSpinner()
            self.showDialog(title: "Error", message: throwable.message ?? "Something went wrong, please try again")
        }
    }
    
    private func proceedWithDownload(mediaData: MediaData) {
        hideSpinner()
        showProgressDialog()
        
        var fileName: String = filenameTextField.text ?? ""
        if fileName.isEmpty {
            fileName = mediaData.tweetId + "-" + "\(Date().currentTimeMillis())" + ".mp4"
        } else {
            fileName = fileName + "-" + "\(Date().currentTimeMillis())" + ".mp4"
        }

        downloadManager = DownloadManager(fileName: fileName)
        downloadManager?.delegate = self

        guard let mediaUrl = URL(string: mediaData.downloadLink) else {
            hideProgressDialog()
            hideSpinner()
            showDialog(title: "Error", message: "Something went wrong. Please try again.")
            return
        }

        let task = downloadManager?.session.downloadTask(with: mediaUrl)
        task?.resume()
    }
    
    private func showDialog(title: String?, message: String, action: UIAlertAction? = nil) {
        let ac = UIAlertController(title: title, message: message, preferredStyle: .alert)
        if action != nil {
            ac.addAction(action!)
        }
        ac.addAction(UIAlertAction(title: "OK", style: .default))
        present(ac, animated: true)
    }
    
    private func showProgressDialog() {
        progressView.center = progressContainerView.center
        progressContainerView.addSubview(progressView)
        AppDelegate.shared?.window?.rootViewController?.view.addSubview(progressContainerView)
    }
    
    private func hideProgressDialog() {
        progressContainerView.removeFromSuperview()
    }
    
    private func showSpinner() {
        spinnerView = displaySpinner(onView: AppDelegate.shared?.window?.rootViewController?.view ?? self.view)
    }
    
    private func hideSpinner() {
        removeSpinner(spinner: spinnerView)
    }

    private func isValidTweetUrl(_ tweetUrl: String) -> Bool {
        tweetUrl.contains("twitter.com/")
    }
}

// MARK: DownloadManagerDelegate

extension MainViewController: DownloadManagerDelegate {
    
    func onProgressChanged(progress: Float) {
        progressView.setProgress(progress, animated: true)
    }
    
    func onDownloadCompleted(fileURL: URL) {
        Analytics.logEvent("complete_download", parameters: [:])
        hideProgressDialog()
        if fileURL.absoluteString.hasSuffix(".mp4") || fileURL.absoluteString.hasSuffix(".MP4") {
            showDialog(title: "Success", message: "Download Completed", action: UIAlertAction(title: "Play Video", style: .default, handler: { [weak self] _ in
                let player = AVPlayer(url: fileURL)
                let playerController = AVPlayerViewController()
                playerController.player = player
                self?.present(playerController, animated: true) {
                    player.play()
                }
            }))
        } else {
            showDialog(title: "Success", message: "Download Completed")
        }
    }
    
    func onDownloadFailed(error: String) {
        hideProgressDialog()
        showDialog(title: "Error", message: "\(error)")
    }
}

// MARK: UITextFieldDelegate

extension MainViewController: UITextFieldDelegate {
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.view.endEditing(true)
    }

    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        self.view.endEditing(true)
        return false
    }
}
