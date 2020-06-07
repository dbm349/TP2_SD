# TP2-SD
Trabajo Práctico 2 de Sistemas Distribuidos

## Ejercicio 1


## Ejercicio 2

Correr las clases en el siguiente orden para forzar el error de actualizacion: 
* ExtraccionServer 
* DepositoServer 
* ClienteDeposito 
* ClienteExtraccion 
* ClienteExtraccion 

Para solucionar el ejercicio A lo que se hizo fue sincronizar las partes de código en las que se lee, procesa escribe en el archivo de saldos utilizando "synchronized".
De esta manera las transacciones se fuerzan para que sean atómicas y que no se modifiquen entre ellas.

## Ejercicio 3


## Ejercicio 4
  
Para correr el ejercicio centralizado se debe ejecutar el "main" que se encuentra en el paquete “Ejercicio4.Centralizado”, utiliza una imagen que por defecto se encuentran en el repositorio llamada "ejemplo.png".  
Para correr el ejercicio Distribuido para el cual se utilizo el sistema RabbitMQ ejecutar: en primer lugar la clase "Maestro" del paquete “Ejercicio4.Distribuido” y luego la clase "Cliente".  

Se implemento un sistema de colas:  
* queueTrabajos: En esta cola el Servidor maestro coloca todos los trozos de imágenes que previamente corto.  
* queueEnProceso: En esta cola los worker colocan los bytes de la imagen con la cual están trabajando.  
* queueTerminados: En esta cola los worker ingresan los trozos de imágenes ya procesadas con el filtro.  

Funcionamiento:  
1. El Maestro inicia, crea las colas, publica sus servicios RMI y queda a la espera.  
2. El cliente inicia, invoca la función que publico el Maestro.  
3. El Maestro toma el tiempo de inicio, purga las colas, troza las imágenes en una cantidad definida, publica todos los trozos en queueTrabajos e instancia una cantidad definida de Workers. Se quedará esperando que la cantidad de mensajes de la cola “queueTerminados” sea igual a la cantidad de trozos que el realizo.  
4. El worker una vez instanciado pregunta la cantidad de trabajos que hay (cantidad de mensajes de la cola “queueTrabajos”, luego pregunta la cantidad de mensajes de la cola  “queueTerminados” (este será su punto de corte). Toma un mensaje de la cola de trabajos, compara si los bytes de ese mensaje son iguales a los bytes de cada mensaje de la cola “en proceso”, si no encuentra que haya uno igual, coloca esos bytes en la cola en proceso y empieza a realizar el aplique del filtro. Si encuentra que hay uno igual, toma otro mensaje de la cola “queueTrabajos”. Una vez que la cantidad de mensajes de terminados es igual a trabajos termina su ejecución.  
5. El Maestro trae todos los mensajes de queueTerminados y los ordena (gracias a un atributo de la clase Imagen que se utiliza para saber que nro. de trozo es). Une todos los trozos ordenadamente y devuelve al cliente la imagen con el filtro Sobel.