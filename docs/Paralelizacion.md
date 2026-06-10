# Informe de Paralelización — Proyecto Final

## Integrantes del grupo

| Nombre completo              | Código    | Correo institucional                              |
|------------------------------|-----------|---------------------------------------------------|
| Aura Maria Pelaez Luna       | 202459422 | aura.pelaez@correounivalle.edu.co                |
| Valentina Valencia Lopez     | 202459626 | valentina.valencia.lopez@correounivalle.edu.co   |

---

## 1. Estrategia de paralelización

### 1.1 `choquesPar`

**Dimensión dividida:** índices del vector de cursos en el rango $[\text{desde}, \text{hasta})$.

**Primitiva:** `parallel(aux(desde, mitad), aux(mitad, hasta))`.

**Combinación:** Los choques dentro de cada mitad se calculan en paralelo. Adicionalmente se calculan los **choques cruzados** (pares $(i,j)$ donde $i$ pertenece a la mitad izquierda y $j$ a la derecha) con una comprensión de listas. La operación de combinación es la suma entera, que es asociativa y conmutativa.

**Umbral:** Cuando el tamaño del segmento es $\le 2$, se usa la versión secuencial.

### 1.2 `desperdicioPar`

**Dimensión dividida:** índices del vector de cursos en $[\text{desde}, \text{hasta})$.

**Primitiva:** `task { desperdicioEnRango(desde, mitad) }` y `task { desperdicioEnRango(mitad, hasta) }`, con `.join()` al final.

**Combinación:** suma entera de los desperdicios de cada mitad. Asociativa y conmutativa.

**Caso base:** tamaño 1 — evaluación directa del curso individual.

### 1.3 `movilidadPar`

**Dimensión dividida:** posiciones en el vector `porHorario` (cursos ya ordenados por inicio).

**Primitiva:** `task { sumarDistancias(desde, mitad) }` y `task { sumarDistancias(mitad, hasta) }`.

**Combinación:** suma de las dos mitades **más** la distancia de enlace $D[\alpha_{\sigma_{\text{mitad}-1}}, \alpha_{\sigma_{\text{mitad}}}]$, que conecta el último elemento de la mitad izquierda con el primero de la derecha.

**Corrección de la combinación:** la cadena de distancias es $\sum_{j=0}^{k-2} D[\cdots]$. Al dividir en $[\text{desde}, \text{mitad})$ y $[\text{mitad}, \text{hasta})$, falta exactamente el par $(\text{mitad}-1, \text{mitad})$, que se agrega explícitamente.

### 1.4 `generarAsignacionesPar`

**Dimensión dividida:** rango de valores de aula $[\text{desde}, \text{hasta})$ para el primer curso.

**Primitiva:** `parallel(construir(desde, mitad), construir(mitad, hasta))`.

**Combinación:** concatenación `++` de vectores de asignaciones. Asociativa sobre `Vector`.

**Observación:** las sub-asignaciones para los $n-1$ cursos restantes se generan recursivamente de forma secuencial; la paralelización actúa sobre la distribución de valores del primer curso.

### 1.5 `asignacionOptimaPar`

**Dimensión dividida:** rango de índices en el vector de candidatas $[\text{desde}, \text{hasta})$.

**Primitiva:** `parallel(minimoEnRango(desde, mitad), minimoEnRango(mitad, hasta))`.

**Combinación:** se retorna la tupla con menor costo entre las dos mitades. La operación es correcta porque $\min$ es asociativo y conmutativo.

---

## 2. Análisis con la Ley de Amdahl

Sea $p$ la fracción del trabajo que puede ejecutarse en paralelo y $k$ el número de núcleos lógicos disponibles. La aceleración teórica máxima es:

$$S_{\max} = \frac{1}{(1 - p) + \dfrac{p}{k}}$$

### Estimación de $p$ por función

| Función | Fracción paralela $p$ estimada | Notas |
|---------|-------------------------------|-------|
| `choquesPar` | ≈ 0.85 | Hay sobrecarga por los choques cruzados |
| `desperdicioPar` | ≈ 0.90 | División simple sin trabajo cruzado |
| `movilidadPar` | ≈ 0.88 | Necesita calcular distancia de enlace |
| `generarAsignacionesPar` | ≈ 0.80 | Sub-asignaciones siguen siendo secuenciales |
| `asignacionOptimaPar` | ≈ 0.92 | División pura del espacio de búsqueda |

Para $k = 4$ núcleos y $p = 0.90$:
$$S_{\max} = \frac{1}{0.10 + 0.225} \approx 3.08$$

Para $k = 8$ núcleos y $p = 0.90$:
$$S_{\max} = \frac{1}{0.10 + 0.1125} \approx 4.71$$

---

## 3. Resultados empíricos

> **Condiciones de medición:** semilla no fijada (Random por defecto), 12 núcleos lógicos, JVM OpenJDK 21.0.7 (Eclipse Adoptium), Scala 2.13.11.

### 3.1 Conteos: `choques`, `desperdicio`, `movilidad`

| Cursos $n$ | Aulas $m$ | `choques` (ms) | `choquesPar` (ms) | Aceleración (%) |
|------------|-----------|----------------|-------------------|-----------------|
| 100  | 5 | 0,35   | 6,90   | -94,9%  |
| 500  | 5 | 5,91   | 19,85  | -70,2%  |
| 1000 | 5 | 69,37  | 26,91  | +157,8% |
| 5000 | 5 | 520,45 | 356,78 | +45,9%  |

| Cursos $n$ | Aulas $m$ | `desperdicio` (ms) | `desperdicioPar` (ms) | Aceleración (%) |
|------------|-----------|--------------------|-----------------------|-----------------|
| 100  | 5 | 0,04 | 0,46 | -90,6% |
| 500  | 5 | 0,02 | 1,06 | -98,0% |
| 1000 | 5 | 0,04 | 2,30 | -98,4% |
| 5000 | 5 | 0,24 | 8,65 | -97,2% |

| Cursos $n$ | Aulas $m$ | `movilidad` (ms) | `movilidadPar` (ms) | Aceleración (%) |
|------------|-----------|------------------|---------------------|-----------------|
| 100  | 5 | 0,92 | 0,57 | +60,0% |
| 500  | 5 | 1,43 | 1,79 | -20,2% |
| 1000 | 5 | 1,42 | 3,81 | -62,7% |
| 5000 | 5 | 9,93 | 8,10 | +22,6% |

### 3.2 Generación de asignaciones

| Cursos $n$ | Aulas $m$ | `generarAsignaciones` (ms) | `generarAsignacionesPar` (ms) | Aceleración (%) |
|------------|-----------|---------------------------|-------------------------------|-----------------|
| 3 | 3 | 0,28 | 0,47 | -39,5% |
| 4 | 3 | 0,11 | 0,34 | -68,0% |
| 5 | 3 | 0,20 | 0,74 | -72,4% |
| 6 | 4 | 1,01 | 2,12 | -52,5% |
| 7 | 4 | 5,96 | 3,99 | +49,4% |

### 3.3 Asignación óptima

| Cursos $n$ | Aulas $m$ | `asignacionOptima` (ms) | `asignacionOptimaPar` (ms) | Aceleración (%) |
|------------|-----------|------------------------|---------------------------|-----------------|
| 4 | 3 | 1,31   | 0,86   | +52,4%  |
| 6 | 4 | 18,41  | 23,62  | -22,0%  |
| 7 | 5 | 187,98 | 56,70  | +231,6% |
| 8 | 5 | 585,26 | 297,23 | +96,9%  |

---

## 4. Análisis de los resultados

### 4.1 ¿Cuándo conviene paralelizar?

Basados en los datos de las tablas anteriores:

- Para instancias pequeñas ($n \le 4$, $m \le 3$): el paralelismo **no es beneficioso** porque la sobrecarga de crear y sincronizar tareas (`task`/`parallel`) supera el tiempo de cómputo real. Se observa una **aceleración negativa** (mayor tiempo que la versión secuencial).

- Para instancias medianas ($n \approx 6$, $m \approx 4$): los resultados son mixtos. `asignacionOptimaPar` con n=6 mostró aceleración negativa (-22,0%) porque 4.096 candidatas aún no son suficientes para amortizar la sobrecarga. Sin embargo, `generarAsignacionesPar` con n=7,m=4 ya muestra ganancia (+49,4%).

- Para instancias grandes ($n \ge 7$, $m = 5$): el paralelismo ofrece **ganancias significativas**, llegando a +231,6% para `asignacionOptimaPar` con n=7,m=5 (78.125 candidatas).

### 4.2 Sobrecarga en instancias pequeñas

Las funciones `choquesPar`, `desperdicioPar` y `movilidadPar` tienen aceleración negativa para vectores pequeños ($n < 500$) porque la creación de `task` implica sincronización en la JVM que es más costosa que la operación misma. El caso más extremo es `desperdicioPar`, que nunca supera a la versión secuencial en ningún tamaño medido, porque la operación es un recorrido lineal con constante muy pequeña.

### 4.3 Paralelismo en datos vs. paralelismo en tareas

- `desperdicioPar` y `movilidadPar` implementan **paralelismo en datos**: dividen el conjunto de datos y aplican la misma operación en paralelo. Son eficientes cuando los datos son grandes.
- `asignacionOptimaPar` y `generarAsignacionesPar` implementan **paralelismo en tareas**: dividen el espacio de búsqueda o construcción. Son eficientes cuando el número de candidatas es grande.

### 4.4 Conclusión

El umbral a partir del cual la paralelización es rentable es aproximadamente:
- Para `choquesPar`: $n > 500$ cursos (complejidad cuadrática O($n^2$)).
- Para `desperdicioPar`: no fue rentable en ningún tamaño medido (operación O($n$) con constante mínima).
- Para `movilidadPar`: comportamiento irregular; rentable solo en n=100 y n=5000.
- Para generación de asignaciones: $m^n > 10.000$ candidatas aproximadamente.
- Para la asignación óptima: $n \ge 7$, $m \ge 5$ (más de 78.125 candidatas).

Dentro de las restricciones del proyecto ($n \le 8$, $m \le 5$), la paralelización es **beneficiosa** para las instancias más grandes y **contraproducente** para las más pequeñas, en línea con la predicción de la Ley de Amdahl.

---

## 5. Código de medición sugerido

```scala
import org.scalameter._

val cursos  = AsignacionAulas.cursosAlAzar(n)
val aulas   = AsignacionAulas.aulasAlAzar(m)
val dist    = AsignacionAulas.distanciasAlAzar(m)
val pesos   = (100, 100, 1, 1)

val timeSeq = measure { AsignacionAulas.asignacionOptima(cursos, aulas, dist, pesos) }
val timePar = measure { AsignacionAulasPar.asignacionOptimaPar(cursos, aulas, dist, pesos) }

println(s"n=$n m=$m Secuencial: $timeSeq ms  Paralelo: $timePar ms  " +
        s"Aceleración: ${(timeSeq.value / timePar.value - 1) * 100}%")
```