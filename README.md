# PolizaDeSeguros

Para correr la aplicacion debemos crear la base de datos "polizadeseguros" y descomentar esta linea en el properties

spring.datasource.url=jdbc:mysql://${DATABASE_HOST}:${DATABASE_PORT}/${DATABASE_NAME}?useSSL=false&allowPublicKeyRetrieval=true

Para correr los test debemos comentar la linea anterior y descomentar esta linea
spring.datasource.url=jdbc:mysql://localhost:3306/polizadeseguros?useSSL=false&allowPublicKeyRetrieval=true
