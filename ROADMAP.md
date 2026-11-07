# Roadmap

TwittaSave has been quiet since 2021, and downloading no longer works (#15). This roadmap gets the project building, tested and up to date first, then restores downloading once the API question below is settled.

## 1. Get the build working again

The toolchain is from 2020 and `jcenter()` has shut down, so the project may not build as it is.

- [ ] Update Gradle (6.7.1), the Android Gradle Plugin (4.1.3) and Kotlin (1.4.10) to current versions
- [ ] Replace `jcenter()` with `mavenCentral()`
- [ ] Move from Ktor 1.4.2 to a current Ktor version
- [ ] Replace the deprecated `native-mt` coroutines with the current release
- [ ] Move kotlinx.serialization off the 1.0.0 release candidate
- [ ] Update the Spotless and ktlint setup
- [ ] Take the useful changes from #8, then close it

## 2. Tests and CI

- [ ] Replace the template example tests with real ones
- [ ] Test tweet link parsing (`TwitterUtil.getTweetId`)
- [ ] Test media selection: highest-bitrate MP4, and errors for tweets with no video or GIF
- [ ] Test API error handling with a mocked HTTP client
- [ ] Replace Travis CI with GitHub Actions: build, test and lint on every pull request
- [ ] Update the build badge in the README

## 3. Support x.com links

Links from the X app use `x.com`, and `getTweetId` rejects anything without `twitter.com/`.

- [ ] Accept `x.com`, `twitter.com` and `mobile.twitter.com` links
- [ ] Handle `/i/status/` links and links with extra query parameters
- [ ] Resolve `t.co` short links
- [ ] Cover every format with tests

## 4. Code health

- [ ] Create one `HttpClient` and reuse it, instead of building a new one on every access
- [ ] Replace the `CoroutineScope(...).launch` callbacks in `TwitterClient` with suspend functions that can be cancelled
- [ ] Stop logging request headers, which include the auth token, in debug builds
- [ ] Put the tweet source behind an interface, so a new source can be added without touching the apps

## 5. Catch up with modern Android

- [ ] Raise `targetSdkVersion` from 30 to the current requirement
- [ ] Save downloads through `MediaStore` instead of `WRITE_EXTERNAL_STORAGE`
- [ ] Replace the clipboard auto-listen service, which Android 10+ blocks from reading the clipboard in the background, with the share sheet the app already supports
- [ ] Replace RxDownloader with the platform `DownloadManager` or WorkManager
- [ ] Raise `minSdkVersion` from 16 to a realistic level

## 6. Docs and releases

- [ ] Update the README setup steps for the current toolchain and API access
- [ ] Add a CHANGELOG
- [ ] Build signed release APKs with GitHub Actions

## Later: restore downloading (#15)

The app uses the v1.1 `statuses/show` endpoint with app-only auth, which X no longer offers on its free access.

- [ ] Decide on tweet access through the official X API
- [ ] Add a tweet source for the chosen API behind the interface from section 4
- [ ] Show clear messages for auth errors, rate limits and deleted or private tweets
- [ ] Download images as well as videos and GIFs (#13)
- [ ] Download every media item in a tweet, not just the first

## Later: iOS release

- [ ] Raise the deployment target from iOS 11.2
- [ ] Replace the custom `packForXcode` task with the current Kotlin Multiplatform setup for Xcode
- [ ] Move from CocoaPods to Swift Package Manager
- [ ] Add a share extension so links can be sent straight from the X app
- [ ] Prepare an App Store release
