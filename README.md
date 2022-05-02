# TeslaAndroidAuth
Lightweight Android library for Tesla SSO Authentication based on [Tesla JSON API (Unofficial)](https://tesla-api.timdorr.com)

## Setup

```
repositories {
    mavenCentral()
}

dependencies {
    implementation("energy.octopus:tesla-android-auth:$teslaAndroidVersion")
}
```

## Usage

Supports only **Compose** for now

### Tesla
```
TeslAuth(
        onError = {
            // Handle error
        },
        onSuccess = {
            // Handle success
        },
        loadingIndicator = {
            // Your custom loading indicator composable
        }
    )
```
### Ohme
```
OhmeAuth(
        clientId = yourOhmeClientId,
        redirectUri = yourOhmeRedirectUri,
        state = ohmeState,
        onError = {
            // Handle error
        },
        onSuccess = {
            // Handle success
        },
        loadingIndicator = {
            // Your custom loading indicator composable
        }
    )
```
