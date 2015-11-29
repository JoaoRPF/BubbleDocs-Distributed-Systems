# Projecto de Sistemas Distribuídos #

## Primeira entrega ##

Grupo de SD Nº 8

Ivo Pires 		73977	ivopires.93@gmail.com

Raquel Cristóvão 	76513	rmcristovao@gmail.com

João Ferreira 		76390	joaof94@hotmail.com



Repositório:
[tecnico-softeng-distsys-2015/T_00_00_08-project](https://github.com/tecnico-softeng-distsys-2015/T_00_00_08-project/)


-------------------------------------------------------------------------------

## Serviço SD-ID 

### Instruções de instalação 

[0] Iniciar sistema operativo
Iniciar o Windows


[1] Iniciar servidores de apoio

JUDDI:
> cd %CATALINA_HOME%/bin
> startup.bat

[2] Criar pasta temporária

> cd Desktop
> mkdir projecto1-SD

[3] Obter versão entregue

> cd Desktop/projecto1-SD
> git clone -b SD-ID_R_2 https://github.com/tecnico-softeng-distsys-2015/T_00_00_08-project/


[4] Construir e executar **servidor SD-ID**

> cd Desktop/projecto1-SD/T_00_00_08/sd-id
> mvn clean generate-sources
> mvn compile exec:java 

[5] Construir e executar **servidor SD-STORE**
(deverá ser lançado um por consola)

> cd Desktop/projecto1-SD/T_00_00_08/sd-store
> mvn clean generate-sources
> mvn compile exec:java -Dinstance=X 
X - valor compreendido entre 0 e 9


[6] Construir **cliente*

> cd Desktop/projecto1-SD/T_00_00_08/sd-id-cli
> mvn clean generate-sources
> mvn compile exec:java


-------------------------------------------------------------------------------

### Instruções de teste: ###


[1] Executar **cliente de testes**

> cd Desktop/projecto1-SD/T_00_00_08/sd-id-cli
> mvn test


[2] Executar **testes do servidor**

> cd Desktop/projecto1-SD/T_00_00_08/sd-id
> mvn test


-------------------------------------------------------------------------------
**FIM**