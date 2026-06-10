# Conclusiones — Proyecto Final

## Integrantes del grupo
| Nombre completo              | Código    | Correo institucional                              |
|------------------------------|-----------|---------------------------------------------------|
| Aura Maria Pelaez Luna       | 202459422 | aura.pelaez@correounivalle.edu.co                |
| Valentina Valencia Lopez     | 202459626 | valentina.valencia.lopez@correounivalle.edu.co   |

---

## 1. Sobre el problema y la solución funcional

El problema de la asignación óptima de aulas es un problema de búsqueda exhaustiva en un espacio de tamaño $m^n$. Aunque esto lo hace intratable para valores grandes de $n$ y $m$, resulta adecuado para ilustrar cómo la programación funcional pura —con tipos inmutables, recursión de cola y funciones de alto orden— permite expresar soluciones correctas y legibles.

La representación del problema como tipos alias de tuplas (`Curso`, `Aula`, `Asignacion`) facilitó el razonamiento formal sobre la corrección, ya que cada función opera sobre estructuras bien definidas y sin efectos secundarios.

El uso de `@tailrec` en las funciones recursivas de cola (`choques`, `capacidadFallida`, `desperdicio`, `movilidad`) fue fundamental para garantizar que el programa no sufra desbordamiento de pila con instancias de tamaño moderado.

---

## 2. Sobre la corrección

La argumentación por inducción estructural resultó ser una herramienta natural para verificar las funciones recursivas del proyecto:

- Las funciones que operan sobre índices (como `choques`) se argumentan por inducción sobre $n = |\text{cursos}|$.
- Las funciones que acumulan resultados (como `desperdicio` y `capacidadFallida`) tienen invariantes de acumulador que facilitan la demostración.
- `generarAsignaciones` se argumenta directamente por inducción sobre $n$, mostrando que la cardinalidad del resultado es siempre $m^n$.

Un hallazgo importante es que la corrección de `asignacionOptima` depende directamente de la corrección de `generarAsignaciones` y `costoAsignacion`. La composición de funciones correctas produce una solución correcta.

---

## 3. Sobre la paralelización

La paralelización con `parallel` y `task` del paquete `common` demostró ser sencilla de implementar pero requiere criterio para decidir cuándo aplicarla:

**Lo que funcionó bien:**
- La división del espacio de búsqueda en `asignacionOptimaPar` es casi perfectamente paralelizable, ya que cada mitad es independiente.
- `desperdicioPar` se beneficia mucho del paralelismo porque la operación sobre cada curso es completamente independiente.

**Lo que presentó desafíos:**
- `choquesPar` requiere calcular los **choques cruzados** entre las dos mitades, lo que añade trabajo adicional y puede cancelar parte del beneficio en instancias pequeñas.
- `movilidadPar` requiere añadir la **distancia de enlace** entre las mitades, lo que obliga a un cuidado especial en la combinación de resultados.

**La Ley de Amdahl en la práctica:** Las fracciones paralelizables estimadas (entre 0.80 y 0.92) sugieren aceleraciones teóricas de 2.5× a 4× con 4 núcleos. Sin embargo, la sobrecarga de gestión de tareas en la JVM hace que, para instancias pequeñas, las versiones paralelas sean más lentas.

---

## 4. Lecciones aprendidas

1. **Inmutabilidad y corrección van de la mano.** El uso de `Vector` inmutable y funciones puras hizo que el razonamiento sobre corrección fuera directo y que los errores de estado compartido fueran imposibles.

2. **La recursión de cola es indispensable.** Sin `@tailrec`, las funciones que recorren vectores grandes podrían causar `StackOverflow`. Scala garantiza la optimización de llamadas de cola solo cuando se anota explícitamente.

3. **El paralelismo tiene un umbral de rentabilidad.** No basta con dividir el trabajo para obtener ganancias: el costo de crear tareas, sincronizarlas y combinar resultados puede superar el ahorro para instancias pequeñas. Es necesario medir empíricamente.

4. **La Ley de Amdahl tiene limitaciones prácticas.** La fracción paralela teórica no considera la sobrecarga de la JVM, la caché del procesador ni los efectos de la memoria compartida. Los speedups empíricos siempre son menores que los teóricos.

5. **La programación funcional facilita la paralelización.** La ausencia de efectos secundarios y el uso de estructuras inmutables hacen que la paralelización sea segura por construcción: no hay condiciones de carrera posibles.

---

## 5. Trabajo futuro

- **Optimización con heurísticas:** La búsqueda exhaustiva es exponencial. Para instancias grandes, se podría explorar búsqueda local, algoritmos genéticos o ramificación y poda.
- **Paralelismo a nivel de datos con colecciones paralelas:** El uso de `.par` sobre `Vector` podría simplificar la implementación de `desperdicioPar` y `choquesPar`.
- **Umbral adaptativo:** En lugar de usar `<= 2` como umbral fijo, se podría calcular dinámicamente el umbral óptimo según el número de núcleos disponibles y el tamaño del problema.