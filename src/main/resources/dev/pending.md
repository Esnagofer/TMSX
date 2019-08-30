NICE TO HAVE
============
Refactor general: extraer componentes den XML y cargar segregar controladores independientes.

BREAK-POINTS
============
Gestionar el UI de BP en la CodeArea <--> modelo [Falta recoger click/menú y lanzar evento]
Utilizar EVENTOS de UI para la comunicación desacoplada.
Al cargar un projecto habilitar menús para compilar/debugar y rellenar info de proyecto.
Preparar programa para gestionar BP.

DEBUG
=====
El debug debe poder hacerse desde un item del TreeView (botón derecho + menú) --> esto crea un exec configuration.
Por ahora ésto es más sencillo que crear una desde cero en el menú correspondiente: en el menú debe haber un histórico de las que se han utilizado (igual que IntelliJ o Eclipse).
=====================================================================================================
Diseñar la vista de debug y hacerla visible cuando se esté debugando.
Implementar la ejecución de un debug y verificar que se carga bien todo el proceso, bp,etc
=====================================================================================================
Mejoras generales de aspecto y usabilidad del IDE: iconos en menús, etc, etc. 


DONE
Al seleccionar un fichero comprobar que el tab NO existe --> si existe dar FOCO.
Al seleccionar un fichero, si el TAB ya está abiero --> NO abrir de nuevo = dar FOCO.
