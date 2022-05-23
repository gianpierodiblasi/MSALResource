# MSALResource
A extension to wrap the Microsoft Authentication Library (MSAL) for Java.

**This Extension is provided as-is and without warranty or support. It is not part of the PTC product suite and there is no PTC support.**

## Description
This extension adds a Resource object wrapping the Microsoft Authentication Library (MSAL) for Java.

## Services
- *getAccessTokenByPFXCertificate*: returns an access token based on a certificate
  - input
    - clientId: the Client ID (Application ID) of the application as registered in the application registration portal (portal.azure.com) - STRING
    - authority: the URL of the authenticating authority or security token service (STS) from which acquire security tokens (ex. https://login.microsoftonline.com/{tenantId}/) - STRING
    - scope: the scope application is requesting access to (ex. https://analysis.windows.net/powerbi/api/.default) - STRING
  - output: STRING

## Configuration Tables
  - PFXCertificateParameters
    - pfxCertificateFileRepository: the repository containing the PCKS12 formatted certificate - THINGNAME (thingTemplate = FileRepository)
    - pfxCertificatePath: the path of the PCKS12 formatted certificate - STRING
    - pfxCertificatePassword: the password of the PCKS12 formatted certificate - PASSWORD

## Dependencies
  - Microsoft Authentication Library (MSAL) for Java - [link](https://github.com/AzureAD/microsoft-authentication-library-for-java)

## Donate
If you would like to support the development of this and/or other extensions, consider making a [donation](https://www.paypal.com/donate/?business=HCDX9BAEYDF4C&no_recurring=0&currency_code=EUR).
