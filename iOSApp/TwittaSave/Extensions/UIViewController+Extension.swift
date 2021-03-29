//
//  UIViewControllerExt.swift
//  TwittaSave
//
//  Created by emmanuel.kehinde on 12/02/2018.
//  Copyright © 2018 emmanuel.kehinde. All rights reserved.
//

import UIKit

extension UIViewController {
    func displaySpinner(onView : UIView) -> UIView {
        let spinnerView = UIView.init(frame: onView.bounds)
        if #available(iOS 13.0, *) {
            spinnerView.backgroundColor = UIColor.systemBackground.withAlphaComponent(0.8)
        } else {
            spinnerView.backgroundColor = UIColor.init(red: 0.5, green: 0.5, blue: 0.5, alpha: 0.5)
        }
        let activityIndicatorView = UIActivityIndicatorView.init(activityIndicatorStyle: .whiteLarge)
        activityIndicatorView.startAnimating()
        activityIndicatorView.center = spinnerView.center
        activityIndicatorView.color = UIColor.gray
        
        DispatchQueue.main.async {
            spinnerView.addSubview(activityIndicatorView)
            onView.addSubview(spinnerView)
        }
        
        return spinnerView
    }
    
    func removeSpinner(spinner: UIView?) {
        DispatchQueue.main.async {
            spinner?.removeFromSuperview()
        }
    }
}
