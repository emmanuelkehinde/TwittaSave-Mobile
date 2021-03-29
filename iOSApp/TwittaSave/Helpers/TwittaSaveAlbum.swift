//
//  TwittaSaveAlbum.swift
//  TwittaSave
//
//  Created by Emmanuel Kehinde on 26/07/2019.
//  Copyright © 2019 emmanuel.kehinde. All rights reserved.
//

import Foundation
import Photos

class TwittaSaveAlbum {
    
    static let albumName = "TwittaSave"
    static let shared = TwittaSaveAlbum()
    
    var assetCollection: PHAssetCollection!
    
    func initialize() {
        if !isAlbumExists() {
            createAlbum()
        }
    }
    
    func isAlbumExists() -> Bool {
        if let assetCollection = fetchAssetCollectionForAlbum() {
            Logger.log("Album Exists")
            self.assetCollection = assetCollection
            return true
        } else {
            return false
        }
    }
    
    func createAlbum() {
        PHPhotoLibrary.shared().performChanges({
            PHAssetCollectionChangeRequest.creationRequestForAssetCollection(withTitle: TwittaSaveAlbum.albumName)
        }) { success, _ in
            if success {
                self.assetCollection = self.fetchAssetCollectionForAlbum()
            }
        }
    }
    
    func fetchAssetCollectionForAlbum() -> PHAssetCollection! {
        
        let fetchOptions = PHFetchOptions()
        fetchOptions.predicate = NSPredicate(format: "title = %@", TwittaSaveAlbum.albumName)
        let collection = PHAssetCollection.fetchAssetCollections(with: .album, subtype: .any, options: fetchOptions)
        
        if let firstObject: PHAssetCollection = collection.firstObject {
            return firstObject
        }
        
        return nil
    }

    func saveAsset(_ destinationFileURL: URL, onSuccess: @escaping (_ url: URL) -> (), onFailure: @escaping (_ error: Error?) -> ()) {
        
        if isAlbumExists() {
            doSaveAsset(destinationFileURL, onSuccess: onSuccess, onFailure: onFailure)
            return
        }
        
        PHPhotoLibrary.shared().performChanges({
            PHAssetCollectionChangeRequest.creationRequestForAssetCollection(withTitle: TwittaSaveAlbum.albumName)
        }) { success, _ in
            if success {
                self.assetCollection = self.fetchAssetCollectionForAlbum()
                self.doSaveAsset(destinationFileURL, onSuccess: onSuccess, onFailure: onFailure)
            }
        }
    }
    
    func doSaveAsset(_ destinationFileURL: URL, onSuccess: @escaping (_ url: URL) -> (), onFailure: @escaping (_ error: Error?) -> ()) {
        
        if self.assetCollection == nil {
            Logger.log(self.assetCollection)
            return
        }
        
        Logger.log("Saving...")
        PHPhotoLibrary.shared().performChanges({
            if let creationRequest = PHAssetChangeRequest.creationRequestForAssetFromVideo(atFileURL: destinationFileURL) {
                if let assetCollection = self.assetCollection {
                    let addAssetRequest = PHAssetCollectionChangeRequest(for: assetCollection)
                    addAssetRequest?.addAssets([creationRequest.placeholderForCreatedAsset!] as NSArray)
                }
            }
        }) { saved, error in
            if saved {
                Logger.log("Saved")
                
                let fetchOptions = PHFetchOptions()
                fetchOptions.sortDescriptors = [NSSortDescriptor(key: "creationDate", ascending: false)]
                
                let fetchResult = PHAsset.fetchAssets(in: self.assetCollection, options: fetchOptions).firstObject
                PHImageManager().requestAVAsset(forVideo: fetchResult!, options: nil, resultHandler: { (avurlAsset, audioMix, dict) in
                    let newObj = avurlAsset as! AVURLAsset
                    Logger.log(newObj.url)
                    onSuccess(newObj.url)
                })
            } else {
                onFailure(error)
            }
        }
        
    }
}
