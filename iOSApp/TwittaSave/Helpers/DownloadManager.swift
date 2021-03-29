//
//  DownloadManager.swift
//  TwittaSave
//
//  Created by emmanuel.kehinde on 13/02/2018.
//  Copyright © 2018 emmanuel.kehinde. All rights reserved.
//

import UIKit
import Photos

protocol DownloadManagerDelegate: class {
    func onProgressChanged(progress: Float)
    func onDownloadCompleted(fileURL: URL)
    func onDownloadFailed(error: String)
}

class DownloadManager : NSObject, URLSessionTaskDelegate, URLSessionDownloadDelegate {
    
    weak var delegate: DownloadManagerDelegate?
    private var fileName: String!
    
    init(fileName: String) {
        self.fileName = fileName
    }
    
    var session : URLSession {
        get {
            let config = URLSessionConfiguration.background(withIdentifier: "\(Bundle.main.bundleIdentifier!).background")
            
            // Warning: If an URLSession still exists from a previous download, it doesn't create
            // a new URLSession object but returns the existing one with the old delegate object attached!
            return URLSession(configuration: config, delegate: self, delegateQueue: OperationQueue())
        }
    }
    
    func urlSession(_ session: URLSession, downloadTask: URLSessionDownloadTask, didWriteData bytesWritten: Int64, totalBytesWritten: Int64, totalBytesExpectedToWrite: Int64) {
        if totalBytesExpectedToWrite > 0 {
            let progress = Float(totalBytesWritten) / Float(totalBytesExpectedToWrite)
            DispatchQueue.main.async
            {
                self.delegate?.onProgressChanged(progress: progress)
            }
        }
    }
    
    func urlSession(_ session: URLSession, downloadTask: URLSessionDownloadTask, didFinishDownloadingTo location: URL) {
        Logger.log("Download finished: \(location)")
        
        let path = NSSearchPathForDirectoriesInDomains(FileManager.SearchPathDirectory.documentDirectory, FileManager.SearchPathDomainMask.userDomainMask, true)
        let documentDirectoryPath: String = path[0]
        let fileManager = FileManager()
        let imagesDirectoryPath = documentDirectoryPath.appendingFormat("/TwittaSave")
        var objcBool: ObjCBool = true
        let isExist = fileManager.fileExists(atPath: imagesDirectoryPath, isDirectory: &objcBool)
        // If the folder with the given path doesn't exist already, create it
        if isExist == false{
            do{
                try fileManager.createDirectory(atPath: imagesDirectoryPath, withIntermediateDirectories: true, attributes: nil)
            }catch{
                Logger.log("Something went wrong while creating a new folder")
            }
        }
        
        let destinationURLForFile = URL(fileURLWithPath: imagesDirectoryPath.appendingFormat("/" + self.fileName!))
        Logger.log(destinationURLForFile.path)
        
        if !fileManager.fileExists(atPath: destinationURLForFile.path){
            do {
                try fileManager.moveItem(at: location, to: destinationURLForFile)
            }catch{
                Logger.log("An error occurred while moving file to destination url")
            }
        }
        
        TwittaSaveAlbum.shared.saveAsset(destinationURLForFile
            , onSuccess: { (fileURL) in
                DispatchQueue.main.async
                    {
                        self.delegate?.onDownloadCompleted(fileURL: destinationURLForFile)
                }
        }) { (error) in
            DispatchQueue.main.async
                {
                    self.delegate?.onDownloadFailed(error: error?.localizedDescription ?? "Error saving media")
            }
        }
        
    }
    
    func urlSession(_ session: URLSession, task: URLSessionTask, didCompleteWithError error: Error?) {
       session.invalidateAndCancel()
    }
    
}
