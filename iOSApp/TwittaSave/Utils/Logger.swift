//
//  Logger.swift
//  TwittaSave
//
//  Created by Emmanuel Kehinde on 29/08/2020.
//  Copyright © 2020 emmanuel.kehinde. All rights reserved.
//

import Foundation

class Logger {
    
    static func log(_ message: Any) {
        #if DEBUG
        print(message)
        #endif
    }
}
