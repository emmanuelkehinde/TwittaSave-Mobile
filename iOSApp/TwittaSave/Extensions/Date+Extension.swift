//
//  Date.swift
//  TwittaSave
//
//  Created by Emmanuel Kehinde on 26/07/2019.
//  Copyright © 2019 emmanuel.kehinde. All rights reserved.
//

import Foundation

extension Date {
    func currentTimeMillis() -> Int64 {
        return Int64(self.timeIntervalSince1970 * 1000)
    }
}
