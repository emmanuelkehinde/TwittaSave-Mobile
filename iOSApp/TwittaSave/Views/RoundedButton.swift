//
//  RoundedButton.swift
//  TwittaSave
//
//  Created by emmanuel.kehinde on 14/02/2018.
//  Copyright © 2018 emmanuel.kehinde. All rights reserved.
//

import UIKit

@IBDesignable
class RoundedButton: UIButton {
    
    override func awakeFromNib() {
        super.awakeFromNib()
        layer.cornerRadius = 5
    }
}
