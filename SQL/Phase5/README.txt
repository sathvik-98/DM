
1. Compile the OracleJDBC.java using the command

    javac -cp ojdbc8-21.1.0.0.jar OracleJDBC.java 

    javac -cp postgresql-42.6.0.jar OracleJDBC.java 

2. Execute the OracleJDBC using the command

    java -cp ojdbc8-21.1.0.0.jar; OracleJDBC
    java -cp .:postgresql-42.6.0.jar OracleJDBC


3. You should get the following output

        -------- Oracle JDBC Connection Testing ------
        Oracle JDBC Driver Registered!
        You made it, take control of your database now!

        Enter your choice 1 (or) 2 (or) 3
        1 : To view any table data
        2 : To make changes to relations
        3 : To see output of queries

4. You can perform above mentioned operations by following the prompts(after each selection).

Note: The front end implemented is command-line(terminal) driven.


