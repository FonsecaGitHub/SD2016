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
(O juddi nao esta no repositorio, o que fazer aqui?)


[2] Criar pasta temporária
...
mkdir proj
cd proj
...

[3] Obter código fonte do projeto (versão entregue)

```
git clone 
```
(Nao sei fazer isto, mas a release esta aqui https://github.com/tecnico-distsys/A_33-project/releases/tag/1 )


[4] Instalar módulos de bibliotecas auxiliares

```
cd uddi-naming
mvn clean install
```

```
cd ...
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

...


-------------------------------------------------------------------------------

### Serviço BROKER

[1] Construir e executar **servidor**

```
cd broker-ws
mvn clean install
mvn exec:java
```


[2] Construir **cliente** e executar testes

```
cd broker-ws-cli
mvn clean install
```

...

-------------------------------------------------------------------------------
**FIM**
