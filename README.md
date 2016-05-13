# Projeto de Sistemas Distribuídos 2015-2016 #

Grupo de SD 033 - Campus Alameda

Henrique Caldeira 75838 henrique.cal94@gmail.com

Miguel Fonseca 67040 miguel.fonseca1011011@gmail.com

Repositório:
[tecnico-distsys/A_33-project](https://github.com/tecnico-distsys/A_33-project/)

-------------------------------------------------------------------------------

## Instruções de instalação 


### Ambiente

[0] Iniciar sistema operativo

Linux



[1] Iniciar servidores de apoio

JUDDI:
```
...
```


[2] Criar pasta temporária

```
cd ...
mkdir ...
```


[3] Obter código fonte do projeto (versão entregue)

```
git clone -b SD_R2 https://github.com/tecnico-distsys/A_33-project
```


[4] Instalar módulos de bibliotecas auxiliares

```
cd uddi-naming
mvn clean install
```

```
cd key-utilities
mvn clean install
```

```
cd transporter-ws-handlers
mvn clean install
```

```
cd broker-ws-handlers
mvn clean install
```

-------------------------------------------------------------------------------

### Serviço AUTHENTICATION-SERVER

[1] Construir e executar **servidor**

```
cd authentication-server-ws
mvn clean install
mvn exec:java
```

[2] Construir **cliente** e executar testes

```
cd authentication-server-cli
mvn clean install
```


-------------------------------------------------------------------------------

### Serviço TRANSPORTER

[1] Construir e executar **servidor**

```
cd transporter-ws
mvn clean install
mvn exec:java
```

[2] Construir **cliente** e executar testes

```
cd transporter-ws-cli
mvn clean install
```



-------------------------------------------------------------------------------

### Serviço BROKER

[1] Construir e executar **servidor**

```
cd broker-ws
mvn clean install
mvn exec:java
```

Executar broker como serviço backup:
```
mvn exec:java -Dws.backup="true"
```

[2] Construir **cliente** e executar testes

```
cd broker-ws-cli
mvn clean install
```


-------------------------------------------------------------------------------
**FIM**

