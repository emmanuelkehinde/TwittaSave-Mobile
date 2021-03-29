[![Made in Nigeria](https://img.shields.io/badge/made%20in-nigeria-008751.svg?style=flat-square)](https://github.com/acekyd/made-in-nigeria)
[![Build Status](https://travis-ci.org/emmanuelkehinde/TwittaSave-Android.svg?branch=master)](https://travis-ci.org/emmanuelkehinde/TwittaSave-Android)

# TwittaSave

## Download Twitter Videos and Gifs directly to your android or iOS device.

<img src=https://raw.githubusercontent.com/emmanuelkehinde/TwittaSave-Mobile/master/screenshots/logo.png alt="Twittasave Logo" width=180 height=180/>

### Download Link

#### Android - [Download the latest apk file here](https://github.com/emmanuelkehinde/TwittaSave-Android/releases/download/v3.0/twittasave-release.apk)

#### iOS - Pending release

---
### App Interface

#### Android

<img src=https://raw.githubusercontent.com/emmanuelkehinde/TwittaSave-Mobile/master/screenshots/android/home.jpg alt="Twittasave Android Home" width=250 height=500/> <img src=https://raw.githubusercontent.com/emmanuelkehinde/TwittaSave-Mobile/master/screenshots/android/about.jpg alt="Twittasave Android About" width=250 height=500/>

#### iOS

<img src=https://raw.githubusercontent.com/emmanuelkehinde/TwittaSave-Mobile/master/screenshots/iOS/home.png alt="Twittasave iOS Home" width=250 height=500/> <img src=https://raw.githubusercontent.com/emmanuelkehinde/TwittaSave-Mobile/master/screenshots/iOS/about.png alt="Twittasave iOS About" width=250 height=500/>

---
### Tools/Resources used

- [Kotlin Multiplatform](https://kotlinlang.org/lp/mobile/)
- [Ktor](https://ktor.io/)
- [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) 
- [Ktlint](https://github.com/JLLeitschuh/ktlint-gradle)
- [Spotless](https://github.com/diffplug/spotless)
- [Twitter API](https://developer.twitter.com/en/docs/twitter-api)
- [Firebase](https://firebase.google.com/)

---
### Project Setup

#### Android

- Create a new project on Twitter Developer Portal
- Get your `consumer key` and `secret` and place them in `local.properties` as `consumer.key` and `consumer.secret` respectively.
- Create a firebase project and generate a `google-services.json` file
- Place your `google-services.json` file in the android app root folder
- Build and run the app

#### iOS

- Create a new project on Twitter Developer Portal
- Get your `consumer key` and `secret` and place them in the project's `User-Defined` `Build Settings` as `TWITTER_CONSUMER_KEY` and `TWITTER_CONSUMER_SECRET` respectively.
- Create a firebase project and generate a `google-services.plist` file
- Place your `google-services.plist` file in the iOS app project folder
- Build and run the app

---
### How to Contribute
- Fork the project & clone locally.
- Create an upstream remote and sync your local copy before you branch.
- Branch for each separate piece of work.
- Do the work and write good commit messages.
- Push to your origin repository.
- Create a new PR (Pull Request) in GitHub.

---
### Love this work? Show your love :heart: by putting a :star: on this project :v:; or you can buy me a coffee <a href='https://ko-fi.com/P5P0GMV2' target='_blank'><img height='36' style='border:0px;height:36px;' src='https://az743702.vo.msecnd.net/cdn/kofi2.png?v=0' border='0' alt='Buy Me a Coffee at ko-fi.com' /></a>

### License
```
   Copyright (C) 2017 Emmanuel Kehinde

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
