//
//  TextFieldExt.swift
//  TwittaSave
//
//  Created by emmanuel.kehinde on 12/02/2018.
//  Copyright © 2018 emmanuel.kehinde. All rights reserved.
//

import UIKit

extension UITextField {
    
    func wiggle() {
        let wiggleAnim = CABasicAnimation(keyPath: "position")
        wiggleAnim.duration = 0.05
        wiggleAnim.repeatCount = 5
        wiggleAnim.autoreverses = true
        wiggleAnim.fromValue = CGPoint(x: self.center.x - 4.0, y: self.center.y)
        wiggleAnim.toValue = CGPoint(x: self.center.x + 4.0, y: self.center.y)
        layer.add(wiggleAnim, forKey: "position")
    }
    
}
