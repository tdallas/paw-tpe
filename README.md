# paw-tpe

#### To Run Follow These Steps

##### From ROOT in console
- `mvn clean compile`
- `mvn install`

- `cd webapp`
- `mvn jetty:run`
  - jetty runs by default on port 8080
  - if that's in use you can use instead:
    `mvn -Djetty.port=` `<anotherPort>` ` jetty:run`

Check `localhost:<portUsed>` to see if the application is running!

- Credenciales:
	- manager (manager)
		user: manager
		password: password
	- employee (empleado)
		user: employee
		password: password
	- cualquier otro usuario con rol cliente creado mediante una reserva:
		user: <emailIngresado>
		password: <emailIngresado>
