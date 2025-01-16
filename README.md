# Video Streaming App  

## Description  
A full-stack application for video streaming.

**Key features:**

**Upload & View Videos**: Share your creations or enjoy content uploaded by others in a user-friendly interface.

**Engagement Tools:** Like, comment, and subscribe to channels to curate your personalized video feed.

**YouTube Integration:**
Search for your favorite YouTube videos and download them directly to your device.

**AI-Powered Chat Assistant:** Leverage a smart virtual assistant, powered by Llama AI, to get instant answers and insights about the video content you're watching.

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
    spring.security.oauth2.resourceserver.jwt.jwk-set-uri = <your auth0 domain>
    auth0.audience = <your logical API URL>  
    auth0.userInfoEndpoint = <your Auth0 user info endpoint>
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
        flask_server_url: "http://127.0.0.1:5000",
        logical_api_url: <your Logical API URL>  
    };
    ```  
    
### Flask Setup  
1. Create a `config.py` file inside the `backend/flask-server` directory.
2. Set the following properties:  

    ```python
    AUTH0_JWK_SET_URI = <your auth0 domain>
    FRONTEND_SERVER = "http://localhost:4200"
    AUDIENCE = <your Logical API URL>
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
