//
//  UserDefaults+Extensions.swift
//  TwittaSave
//
//  Created by Emmanuel Kehinde on 29/08/2020.
//  Copyright © 2020 emmanuel.kehinde. All rights reserved.
//

import Foundation

extension UserDefaults {
    
    private enum UserDefaultsKeys: String {
        case isClipboardCheckDisabled
    }
    
    var isClipboardCheckDisabled: Bool {
        get {
            return bool(forKey: UserDefaultsKeys.isClipboardCheckDisabled.rawValue)
        }
        set {
            set(newValue, forKey: UserDefaultsKeys.isClipboardCheckDisabled.rawValue)
        }
    }

}
