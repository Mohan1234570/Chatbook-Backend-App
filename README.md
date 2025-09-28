# 📘 Chatbook Application  

Chatbook is a **social media platform** where users can **post, comment, follow, receive notifications, and chat with AI in realtime**.  
Built with **Spring Boot (Backend)** and **React + Redux + MUI (Frontend)**, the app provides a **modern, scalable architecture** for social networking.  

---

## 🚀 Features  

- **User Management**: Registration, login, JWT-based authentication.  
- **Posts & Comments**: Create posts, comment on posts, like posts.  
- **Feed System**: Personalized feed from following users.  
- **Follow/Unfollow**: Connect with other users.  
- **Notifications**: Realtime alerts for likes, comments, follows.  
- **AI Chatbot**: Integrated **OpenAI-powered chatbot** with **SSE streaming** for realtime conversations.  

---

## 🏗️ High-Level Architecture  

```mermaid
flowchart TB
    subgraph Frontend[React + Redux + MUI]
        UI[User Interface] --> APIClient[Axios/Fetch API]
    end

    subgraph Backend[Spring Boot Application]
        Controller[REST Controllers] --> Service[Business Services]
        Service --> Repo[Spring Data JPA Repositories]
        Service --> AIService[OpenAI Service Integration]
        Repo --> DB[(PostgreSQL Database)]
        AIService --> OpenAI[OpenAI API / Spring AI]
    end

    subgraph Notifications[Async Layer]
        EventPublisher[Spring Events] --> Notifier[Notification Service]
    end

    Frontend -->|REST APIs / JSON / SSE| Backend
    Backend --> Notifications
