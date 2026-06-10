## Descripción del desarrollo

El proyecto se desarrolló en Scala 2.13 implementando dos módulos principales:
`AsignacionAulas` con las funciones secuenciales y `AsignacionAulasPar` con sus
versiones paralelas.

La solución secuencial define los tipos base (`Curso`, `Aula`, `Asignacion`,
`Distancias`) e implementa las funciones `solapan`, `choques`, `capacidadFallida`,
`desperdicio`, `movilidad`, `costoAsignacion`, `generarAsignaciones` y
`asignacionOptima` usando operaciones funcionales sobre vectores inmutables
(`flatMap`, `filter`, `map`, `minBy`, `sliding`).

La solución paralela replica cada función aplicando división recursiva del espacio
de trabajo mediante `parallel` y `task` del paquete `common`, siguiendo el patrón
divide-y-vencerás. La función `asignacionOptimaPar` divide el espacio de candidatas
($m^n$) en dos mitades que buscan su mínimo local en paralelo.

Las mediciones empíricas con `System.nanoTime` sobre 12 núcleos lógicos
(JVM OpenJDK 21.0.7) confirmaron que el paralelismo es rentable para instancias
grandes — hasta +231% de aceleración en `asignacionOptimaPar` con $n=7$, $m=5$ —
y contraproducente para instancias pequeñas, en línea con la predicción de la
Ley de Amdahl.

---

## Integrantes del grupo

| Nombre completo              | Código    | Correo institucional                           |
|------------------------------|-----------|------------------------------------------------|
| Aura Maria Pelaez Luna       | 202459422 | aura.pelaez@correounivalle.edu.co              |
| Valentina Valencia Lopez     | 202459626 | valentina.valencia.lopez@correounivalle.edu.co |
