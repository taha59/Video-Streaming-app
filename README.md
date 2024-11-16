# Video Streaming App  

## Description  
A modern full-stack application for video streaming, designed to provide a seamless experience for uploading, viewing, liking, and subscribing to videos. With built-in support for downloading and searching YouTube videos, the platform enhances your video content experience.  

To make content discovery even easier, the app leverages AI to automatically generate short descriptions for uploaded videos.  

---

## Requirements  

### Prerequisites  
Ensure the following tools and services are set up before starting:  
- **Java 17 Runtime**  
- **Node.js v18.20.3**  
- **MongoDB Community Server**  
- **AWS Account** (with an S3 bucket configured)  
- **AWS CLI**  
- **pytubefix**  

---

## Setup  

### AWS Single Sign-On (SSO)  
Configure AWS Single Sign-On by following the guide:  
[Configuring AWS CLI with SSO](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-sso.html)  

### Spring Boot Setup  
1. Create a `secrets.properties` file inside the `resources` directory.  
2. Add the following properties to the file:  

    ```properties
    spring.data.mongodb.uri = <your MongoDB URI>  
    groq.api.key = <your Groq API key>  
    s3.bucket.name = <your S3 bucket name>  
    spring.security.oauth2.resourceserver.jwt.jwk-set-uri = <your OAuth2 resource server>  
    auth0.audience = http://localhost:8080  
    auth0.userInfoEndpoint = <your Auth0 endpoint>  
    ```

### Angular Setup  
1. Navigate to the `youtube-clone-ui` directory.  
2. Generate environment files:  

    ```bash
    ng generate environments
    ```  

3. Edit the `environment.development.ts` file and add the following:  

    ```typescript
    export const environment = {
        auth_authority: <your OAuth authority>,  
        auth_client_id: <your OAuth client ID>,  
        springboot_server_url: "http://localhost:8080",  
        flask_server_url: "http://127.0.0.1:5000"  
    };
    ```  

---

## Build Instructions  

### Build Spring Boot Project  
Run the following command in the Spring Boot project directory:  
```bash
./mvnw clean verify
```
### Build Angular Project  
Run the following command in the `youtube-clone-ui` directory:  
```bash
npm install
```
---

## Start Servers

### Run Spring Boot API
```bash
bash springboot_api.sh
```

### Run Flask API
```bash
bash flask_api.sh
```

### Run Angular Server
```bash
bash angular_server.sh
```

---

## Enjoy!
Access the application at `http://localhost:4200`

