# TeslaAndroidAuth
Lightweight Android library for Tesla SSO Authentication based on [Tesla JSON API (Unofficial)](https://tesla-api.timdorr.com)

## Setup (WIP)

- `mavenCentral` artifact coming soon

## Usage

Supports only **Compose** for now
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
