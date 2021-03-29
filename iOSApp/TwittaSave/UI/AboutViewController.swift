//
//  AboutVC.swift
//  TwittaSave
//
//  Created by emmanuel.kehinde on 14/02/2018.
//  Copyright © 2018 emmanuel.kehinde. All rights reserved.
//

import UIKit

class AboutViewController: UIViewController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    @IBAction func onWebLinkPressed(_ sender: Any) {
        let webAddress = URL(string: "https://twittasave.net")
        UIApplication.shared.open(webAddress! as URL, options: [:], completionHandler: nil)
    }
    
    @IBAction func onDeveloperPressed(_ sender: Any) {
        let twitterAddress = URL(string: "https://twitter.com/emmakoko96")
        UIApplication.shared.open(twitterAddress! as URL, options: [:], completionHandler: nil)
    }

}
