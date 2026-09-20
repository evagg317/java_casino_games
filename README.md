# Distributed Online Casino Platform

A distributed online casino platform supporting Manager and Player functionalities, developed entirely in **Java**. This project was implemented as part of the "Distributed Systems" course (Spring Semester 2025-2026) at the Athens University of Economics and Business (AUEB).

## Architecture
The system is built on a Master-Worker architecture and communicates exclusively via **TCP Sockets**. No external libraries or databases are used, and multithreading synchronization happens through (`synchronized`, `wait`, `notify`).

* **Backend:** Java (Multithreaded TCP Servers)
* **Frontend:** Android Application 
* **Algorithms:** Custom **MapReduce** implementation for game filtering and calculating profit/loss statistics.

## System Components
The distributed environment consists of the following nodes cooperating in real-time:
1. **Master:** Receives client requests and routes them to the appropriate Workers.
2. **Workers:** Store game data in-memory and execute the core game logic (Map process).
3. **Reducer:** Merges statistics and query results from the Workers (Reduce process).
4. **Secured Random Generator (SRG):** An independent TCP Server that generates random numbers for betting outcomes, communicating with Workers via a Producer-Consumer model.

## How to Run
### 1. Start the Backend
Go to the `Backend_Code` directory, compile the Java files, and start the nodes in **separate terminal windows** in the following strict order:
Since this is a distributed system, the components must be started in a specific sequence.
This sequence works only if you use one computer. If you want to use more you must use the right IP address for each "localhost"

Start the components in separate terminal windows in the following order:

Step 1: ```java src.SRGServer 9090```

Step 2: '''java src.ReducerMain 6000'''

Step 3: e.g. for three workers
```
java src.WorkerMain 0 8081 3 games/json localhost 9090
java src.WorkerMain 1 8082 3 games/json localhost 9090
java src.WorkerMain 2 8083 3 games/json localhost 9090
```

Step 4: ''' java src.MasterMain 8080 3 localhost 6000 localhost 8081 localhost 8082 localhost 8083 '''

Step 5: Launch the Clients
'''
java src.ManagerApp localhost (to load new JSON games into the system)
java src.PlayerApp localhost
'''

### 2. Start the Android Client
1. Open the repository root folder in **Android Studio**.
2. Wait for the Gradle sync to complete.
3. Start the app from the HomescreenActivity

## Screenshots
<img width="302" height="665" alt="Screenshot 2026-09-20 at 22 00 18" src="https://github.com/user-attachments/assets/c4c31928-8ff8-4578-bae8-eb538376975d" />
<img width="303" height="669" alt="Screenshot 2026-09-20 at 22 01 02" src="https://github.com/user-attachments/assets/34f56463-b18d-4192-a904-e0d867f5e5dd" />
<img width="302" height="667" alt="Screenshot 2026-09-20 at 22 02 13" src="https://github.com/user-attachments/assets/58691002-a380-4a6a-af24-2303d2837ede" />
<img width="304" height="669" alt="Screenshot 2026-09-20 at 22 02 38" src="https://github.com/user-attachments/assets/5b5f8769-c0c1-4411-a92a-dc4254e8f5f4" />
