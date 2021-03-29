//
//  RoundedTextField.swift
//  TwittaSave
//
//  Created by emmanuel.kehinde on 14/02/2018.
//  Copyright © 2018 emmanuel.kehinde. All rights reserved.
//

import UIKit

@IBDesignable
class RoundedTextField: UITextField {
    
    override func awakeFromNib() {
        super.awakeFromNib()
        layer.borderColor = #colorLiteral(red: 0.6000000238, green: 0.6000000238, blue: 0.6000000238, alpha: 1)
        layer.borderWidth = 1
        layer.cornerRadius = 5
    }
}
