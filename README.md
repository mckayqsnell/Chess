# Full-Stack Chess Application

A terminal-based chess game backed by a robust Java server and relational database, this project showcases best practices in software design, testing, and scalable architecture. It emphasizes modularity, maintainability, and the clear separation of concerns through proven engineering techniques.

## Technologies & Architecture

- **Client (Chess UI)**  
  - A **terminal-based** program for user interaction (prelogin/postlogin/gameplay modes).  
  - **WebSocket** for real-time chess moves, notifications, and observer updates.  
  - Displays a fully annotated chessboard, reflecting whether a user is **White**, **Black**, or simply observing.  

- **Server (Chess Backend)**  
  - **Java** server exposing **HTTP endpoints** (RESTful Web API) for user registration, login, and game management (create, join, list).  
  - **WebSocket** endpoints (`/ws`) to relay updates and notifications between multiple clients in real-time.  
  - **Data Access Layer** hidden behind interfaces (DAOs), allowing easy switching between an **in-memory** or **MySQL** implementation for persistence.  
  - **BCrypt** hashing for secure password storage.  
  - JSON serialization with **Gson** for game state (ChessGame) and client/server messages.

- **Database**  
  - **MySQL** (or in-memory) storage for `UserData`, `GameData`, and `AuthData`.  
  - Automated creation of tables, schema, and handling of indexes at server startup.

## Implementation Highlights

- **Layered Design**: Handlers (HTTP/WebSocket) delegate to **Service** classes, which orchestrate business logic, then persist or retrieve data via DAOs.  
- **Data Modeling**:  
  - `UserData` holds user credentials (securely hashed passwords).  
  - `GameData` tracks the ChessGame state, players, and database IDs.  
  - `AuthData` records authenticated tokens for session management.  
- **ChessGame Logic**:  
  - Validates moves (including check/checkmate logic, optional En Passant and Castling).  
  - Serialized as JSON for storage and transmitted to clients when updated.  
- **Unit & Integration Testing**:  
  - **Service Tests** confirm correctness of server-side logic (registration, login, game creation, etc.).  
  - **Data Access Tests** verify DAO operations for both positive (success) and negative (failure) cases.  
  - **Client Tests** confirm that `ServerFacade` correctly handles all server responses.

## Key Features

- **User Management**: Register, log in/out, maintain sessions with **authTokens**.  
- **Game Management**: Create new games, list existing games, join as White/Black, or observe live gameplay.  
- **Real-time Gameplay**:  
  - **WebSocket** ensures immediate board updates and notifications (connect, moves, resignations).  
  - Automatic broadcast of moves and status (check, checkmate, stalemate) to all participants.  
- **Terminal UI**:  
  - Simple commands for controlling user flow (help, logout, create/list/join games).  
  - Chessboard rendering from a user’s perspective (White or Black) with color-coded squares and pieces.
