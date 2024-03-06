# als-welsh-names-lambda-function

This lambda function downloads the welsh translations of the names of countries and territories to s3.
It will be triggered on a schedule so that the files are as up-to-date as possible.

It requires some environment variables to be set:

| EnvVar                             | Example                                                        | Description                                         |
|------------------------------------|----------------------------------------------------------------|-----------------------------------------------------|
| BUCKET_NAME                        | als-welsh-country-files                                        | The S3 bucket to which the files will be downloaded |
| AWS_REGION                         | eu-west-2                                                      | The region that the bucket should be accessed from  |
| ALS_COUNTRY_NAMES_LINK_TEXT        | Enwau gwledydd – Country names                                 | The link text for the country names file            |
| ALS_OVERSEAS_TERRITORIES_LINK_TEXT | Enwau Tiriogaethau Tramor y DU – UK Overseas Territories names | The link text for the overseas territories file     |
| ALS_CROWN_DEPENDENCIES_LINK_TEXT   | Enwau Dibyniaethau Coron y DU – UK Crown Dependencies names    | The link text for the crown dependencies file       |

### License

This code is open source software licensed under
the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
