# AGILE ASSESSMENT PROJECT
- This project is created and built by Jakarta EE 10, a framework for building enterprise application. If you want to know more about this, please visit to this website: [Jakarta EE 10](https://jakarta.ee/learn/docs/jakartaee-tutorial/current/intro/overview/overview.html)
- In addition, this project used [wildfly-29.0.1-final](https://github.com/wildfly/wildfly/releases/download/29.0.1.Final/wildfly-29.0.1.Final.zip) as a web server to run the project on wildfly server.

# SETTING UP
### RUN BY DOCKER
If you want to run this application by docker, please follow these steps:
- Input "mvn clean package" in folder of application to build .war file.
- Input "wsl" to open docker cli.
- Input "docker compose up" to run docker-compose.yml file and when it finishes, your application will run successfully.

If you haven't installed docker yet on your computer, please follow this file: [Download Docker CLI](https://aavn.sharepoint.com/sites/Eureka/Shared%20Documents/Forms/AllItems.aspx?id=%2Fsites%2FEureka%2FShared%20Documents%2FJuniorClass%2FDocument%5FSharedFolders%2FBYTE2025%5FDocs%2F2025%5FBYTE%5FHow%20To%20Download%20Docker%2Epdf&parent=%2Fsites%2FEureka%2FShared%20Documents%2FJuniorClass%2FDocument%5FSharedFolders%2FBYTE2025%5FDocs)

### RUN MANNUALLY
If this is the first time that you clone and run an application on wildfly server, you can [click here](https://aavn.sharepoint.com/:b:/r/sites/Eureka/Shared%20Documents/JuniorClass/Document_SharedFolders/BYTE2025_Docs/2025_BYTE_Setting%20and%20Running%20Jakarta%20EE%20Application%20on%20Wild.pdf?csf=1&web=1&e=LP69h9) to be guided to run the application on wildfly server.

If you want to clone and run this source code, you must follow these steps:
- Configure the driver to create datasource in wildfly server (based on instruction: [Configuring driver and datasource in Wildfly Server](https://aavn.sharepoint.com/:b:/r/sites/Eureka/Shared%20Documents/JuniorClass/Document_SharedFolders/BYTE2025_Docs/2025_BYTE_Configuring%20driver,%20datasource%20and%20integrating%20into%20application.pdf?csf=1&web=1&e=O8Y6AM)).
- Go to folder application, open terminal and run "mvn clean package" to build .war file.
- Run the wildfly server and upload .war file and deploy it. (based on instruction: [Setting and Running Jakarta EE Project by Wildfly Server](https://aavn.sharepoint.com/:b:/r/sites/Eureka/Shared%20Documents/JuniorClass/Document_SharedFolders/BYTE2025_Docs/2025_BYTE_Setting%20and%20Running%20Jakarta%20EE%20Application%20on%20Wild.pdf?csf=1&web=1&e=LP69h9)

Then, the base url of our application is: http://localhost:8080/{runtime-name}/api/{endpoint}, with:
- runtime-name: the name that you set before deploy file .war (default is agile-assessment).
- endpoint: entity that you want to request.

# DEPENDENCIES
- We use hibernate to interact and manipulate with data stored in database: [hibernate-dependency](https://mvnrepository.com/artifact/org.hibernate.orm/hibernate-core/6.6.3.Final) 
- We use postgresql driver to connect with PostgreSQL: [postgres-driver](https://mvnrepository.com/artifact/org.postgresql/postgresql/42.7.3)
- We use lombok to code easily because it supports a lot in writing code such as automatically generating getter/setter functions, constructors,...: [lombok](https://mvnrepository.com/artifact/org.projectlombok/lombok/1.18.30 )

