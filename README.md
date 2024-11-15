# Video Streaming App
## Description
Full stack video streaming application that uploads videos to AWS S3 service and saves the youtube url and metadata in the mongo db database
## Requirements:
## Prerequisites
- **Java 17 Runtime**
- **Node Js v18.20.3**
- **MongoDB Community server**
- **AWS account with s3 bucket set up**
- **AWS CLI**
- **pytubefix**

## Set up SSO
```aws configure sso```

## Build project
- ```./mvnw clean verify```

## Run servers
```bash start_backend_server.sh```

```bash start_frontend_server.sh```

## Enjoy!
Go to ```localhost:4200``` for the youtube application

