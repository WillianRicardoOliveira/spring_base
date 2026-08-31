# Tenant V1 - Configuracao de Ambiente

## Backend - Integrator

Usar configuracao em formato application.properties.

spring.profiles.active=prod
spring.mvc.locale=pt_BR
server.port=${port:28859}

logging.file.path=/home/willi10700/appservers/private/springboot/logs/
logging.file.name=${logging.file.path}/erp-1.0.0.log
logging.logback.rollingpolicy.max-file-size=3MB
logging.pattern.dateformat=dd/MM/yy HH:mm:ss
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
logging.level.root=INFO

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/willi10700_erp
spring.datasource.username=willi10700_erp_app
spring.datasource.password=***

spring.flyway.enabled=true
spring.jpa.hibernate.ddl-auto=validate

api.security.token.secret=***
api.security.token.issuer=erp-api-homolog
api.security.token.expiration-minutes=480
api.security.refresh-token.expiration-days=7
api.security.refresh-token.cleanup-cron=0 0 * * * *

app.security.login.max-failed-attempts=5
app.security.login.lock-minutes=15
app.security.swagger-public=false

app.cors.allowed-origins=https://willi10700.c44.integrator.host

spring.security.oauth2.resourceserver.jwt.issuer-uri=https://login.microsoftonline.com/***/v2.0
sso.claim.email=preferred_username
sso.audience=***
sso.scope=Employees.Read.All

app.bootstrap.enabled=false

app.convite-organizacao.validade=48h
app.convite-organizacao.url-aceite=https://willi10700.c44.integrator.host/convites/organizacao/aceitar
app.convite-organizacao.remetente=convites@willi10700.c44.integrator.host

spring.mail.host=willi10700.c44.integrator.host
spring.mail.port=465
spring.mail.username=convites@willi10700.c44.integrator.host
spring.mail.password=***
spring.mail.default-encoding=UTF-8
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.ssl.enable=true
spring.mail.properties.mail.smtp.starttls.enable=false
spring.mail.properties.mail.smtp.starttls.required=false
spring.mail.properties.mail.smtp.connectiontimeout=10000
spring.mail.properties.mail.smtp.timeout=10000
spring.mail.properties.mail.smtp.writetimeout=10000
spring.mail.properties.mail.debug=false

## Frontend - Integrator

Build de producao apontando para:
https://api.willi10700.c44.integrator.host

Na pasta publica do frontend, manter .htaccess:

<IfModule mod_rewrite.c>
  RewriteEngine On
  RewriteBase /
  RewriteCond %{REQUEST_FILENAME} -f [OR]
  RewriteCond %{REQUEST_FILENAME} -d
  RewriteRule ^ - [L]
  RewriteRule ^ index.html [L]
</IfModule>