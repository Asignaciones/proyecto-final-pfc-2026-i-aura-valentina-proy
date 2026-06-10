# Informe de Corrección — Proyecto Final

## Integrantes del grupo
| Nombre completo              | Código    | Correo institucional                              |
|------------------------------|-----------|---------------------------------------------------|
| Aura Maria Pelaez Luna       | 202459422 | aura.pelaez@correounivalle.edu.co                |
| Valentina Valencia Lopez     | 202459626 | valentina.valencia.lopez@correounivalle.edu.co   |

---

## Metodología

Para cada función recursiva se aplica **inducción estructural** sobre el conjunto definido recursivamente en el que opera (naturales para índices, vectores para colecciones). Se demuestra:

1. **Caso base:** el programa retorna el valor correcto para la entrada mínima.
2. **Caso inductivo:** asumiendo que el programa es correcto para entradas más pequeñas (hipótesis de inducción, H.I.), se demuestra que también lo es para la entrada general.

---

## 1. `solapan`

### Especificación
$$\text{solapan}(c_1, c_2) = \text{true} \iff \text{ini}_{c_1} < \text{fin}_{c_2} \land \text{ini}_{c_2} < \text{fin}_{c_1}$$

### Argumentación
La función es no recursiva y evalúa directamente la condición de la especificación. Por tanto, su corrección es inmediata: retorna exactamente la conjunción de las dos desigualdades que definen el solapamiento. $\blacksquare$

---

## 2. `choques`

### Especificación

$$\text{CH}^\alpha_C = |\{(i,j) \mid 0 \le i < j < n,\; \alpha_i = \alpha_j \ge 0,\; c_i \text{ solapa con } c_j\}|$$

### Argumentación por inducción sobre $n = |\text{cursos}|$

Sea $P(n)$: `recorrer(0, 1, 0)` retorna $\text{CH}^\alpha_C$ para un vector de $n$ cursos.

**Caso base** $n = 0$: No hay pares posibles, $\text{CH} = 0$. La función entra a `i >= cursos.length` inmediatamente y retorna `acum = 0`. $\checkmark$

**Caso base** $n = 1$: Solo existe el índice 0; `j = 1 >= 1 = length` en la primera llamada, lo que provoca `recorrer(1, 2, 0)`, y luego `i >= length` retorna 0. $\checkmark$

**Caso inductivo:** Para $n = k+1$, asumamos H.I.: la función cuenta correctamente los choques en cualquier subconjunto de $k$ cursos.

La función itera sobre todos los pares $(i, j)$ con $0 \le i < j < k+1$ de forma sistemática:
- Cuando `j >= cursos.length`, avanza a `(i+1, i+2)`, garantizando que se cubren exactamente los pares con $i < j$.
- Para cada par, verifica $\alpha_i \ge 0 \land \alpha_j \ge 0 \land \alpha_i = \alpha_j \land \text{solapan}(c_i, c_j)$, exactamente la condición de la especificación.
- Acumula con cola recursiva, por lo que el resultado final es la suma de todas las condiciones verdaderas.

Así, $P(k+1)$ se satisface. $\blacksquare$

La anotación `@tailrec` garantiza que la función es de cola y no produce `StackOverflow`.

---

## 3. `capacidadFallida`

### Especificación
$$\text{CF}^\alpha_{C,A} = |\{i \mid \alpha_i \ge 0,\; \text{cap}^A_{\alpha_i} < \text{est}^C_i\}|$$

### Argumentación por inducción sobre $n = |\text{cursos}|$

**Caso base** $n = 0$: `ir(0, 0)` con `length = 0` retorna `0`. $\checkmark$

**Caso inductivo:** Para $n = k+1$, H.I.: la función es correcta para los $k$ cursos restantes a partir del índice $i+1$.

En la llamada `ir(i, acum)`:
- Si $\alpha_i \ge 0$ y $\text{cap}(\text{aulas}(\alpha_i)) < \text{est}(\text{cursos}(i))$, entonces `falla = true` y se suma 1 al acumulador.
- Se llama a `ir(i+1, acum + (if falla 1 else 0))`.

Por H.I., `ir(i+1, ...)` cuenta correctamente los fallos de los cursos $i+1, \ldots, n-1$. La suma total es correcta. $\blacksquare$

---

## 4. `desperdicio`

### Especificación
$$\text{DE}^\alpha_{C,A} = \sum_{\substack{i=0 \\ \alpha_i \ge 0}}^{n-1} \max(\text{cap}^A_{\alpha_i} - \text{est}^C_i,\; 0)$$

### Argumentación por inducción sobre $n$

**Caso base** $n = 0$: `ir(0, 0)` retorna `0`. $\checkmark$

**Caso inductivo:** Para $n = k+1$, H.I.: `ir(i+1, 0)` retorna el desperdicio de los cursos $i+1, \ldots, n-1$.

En `ir(i, acum)`:
- Si $\alpha_i \ge 0$, calcula $\text{sobra} = \max(\text{cap}_{\alpha_i} - \text{est}_i, 0)$ y lo suma.
- Si $\alpha_i < 0$, suma 0.
- Llama a `ir(i+1, acum + sobra)`.

La suma acumulada recorre todos los índices exactamente una vez, aplicando la operación de la especificación en cada uno. $\blacksquare$

---

## 5. `movilidad`

### Especificación

Sea $\sigma$ la permutación que ordena los cursos asignados por hora de inicio:
$$\text{MV}^\alpha_{C,A,D} = \sum_{j=0}^{k-2} D[\alpha_{\sigma_j}, \alpha_{\sigma_{j+1}}]$$

### Argumentación

1. El filtrado `cursos.indices.filter(asig(_) >= 0)` selecciona exactamente los cursos asignados. Correcto por definición de `filter`.
2. El ordenamiento `sortBy(i => iniCurso(cursos(i)))` produce $\sigma$. Correcto pues `sortBy` es estable y total sobre enteros.
3. La función `sumar(pos, acum)` itera sobre posiciones consecutivas de `porHorario`:
    - **Caso base** `pos + 1 >= length`: no hay más pares, retorna `acum`. $\checkmark$
    - **Caso inductivo:** agrega `dist(asig(porHorario(pos)))(asig(porHorario(pos+1)))` y avanza. Por H.I. sobre la longitud restante, la suma acumulada es correcta.

$\blacksquare$

---

## 6. `costoAsignacion`

### Especificación
$$\text{CT}^\alpha = w_{CH} \cdot \text{CH} + w_{CF} \cdot \text{CF} + w_{DE} \cdot \text{DE} + w_{MV} \cdot \text{MV}$$

### Argumentación

La función es no recursiva y combina linealmente los resultados de `choques`, `capacidadFallida`, `desperdicio` y `movilidad`, multiplicados por sus respectivos pesos. Dado que cada subfunción es correcta (demostrado arriba), el resultado es correcto por sustitución directa. $\blacksquare$

---

## 7. `generarAsignaciones`

### Especificación

$$\text{generarAsignaciones}(n, m) = \{0, \ldots, m-1\}^n \quad \text{como } \texttt{Vector[\text{Asignacion}]}$$

El tamaño del resultado debe ser $m^n$.

### Argumentación por inducción sobre $n$

**Caso base** $n = 0$: Retorna `Vector(Vector.empty)`, que es $\{()\} = \{0,\ldots,m-1\}^0$. Tamaño $= 1 = m^0$. $\checkmark$

**Caso inductivo:** Para $n = k+1$, H.I.: `generarAsignaciones(k, m)` retorna exactamente $\{0,\ldots,m-1\}^k$ con $m^k$ elementos.

La función hace:
```
aulasPosibles.flatMap { aulaParaCursoN =>
  subAsignaciones.map(sub => aulaParaCursoN +: sub)
}
```

Para cada $j \in \{0,\ldots,m-1\}$, antepone $j$ a cada sub-asignación de $\{0,\ldots,m-1\}^k$, generando $m^k$ asignaciones que comienzan con $j$. Concatenando para los $m$ valores de $j$ se obtienen $m \cdot m^k = m^{k+1}$ asignaciones, que son exactamente $\{0,\ldots,m-1\}^{k+1}$. $\blacksquare$

---

## 8. `asignacionOptima`

### Especificación

$$\text{asignacionOptima}(C,A,D,w) = (\alpha^*, \text{CT}^{\alpha^*}) \quad \text{donde } \alpha^* = \arg\min_{\alpha \in \{0,\ldots,m-1\}^n} \text{CT}^\alpha$$

### Argumentación

1. `generarAsignaciones` produce todas las asignaciones candidatas (demostrado arriba).
2. `buscarMejor` realiza un recorrido lineal de cola manteniendo el mínimo:
    - **Caso base** `resto.isEmpty`: retorna `(mejorHasta, costoMin)`. Correcto pues se han evaluado todas las candidatas.
    - **Caso inductivo:** Compara el costo de `resto.head` con `costoMin`. Si es menor, actualiza; de lo contrario conserva. Por H.I., `buscarMejor(resto.tail, ...)` retorna el mínimo del subconjunto restante.

La combinación garantiza que se retorna el mínimo global de todas las candidatas. $\blacksquare$

---

## Casos de prueba

Los casos de prueba están implementados en `AsignacionAulasTest.scala` y `AsignacionAulasParTest.scala`. A continuación se resumen los valores esperados para los casos más representativos.

### `solapan`

| Test | c1 | c2 | Esperado |
|------|----|----|----------|
| 1 | (C1,0,4,30) | (C2,2,6,20) | `true` (se solapan en [2,4)) |
| 2 | (C1,0,4,30) | (C4,1,3,35) | `true` (se solapan en [1,3)) |
| 3 | (C2,2,6,20) | (C3,5,8,15) | `true` (se solapan en [5,6)) |
| 4 | (C1,0,4,30) | (C3,5,8,15) | `false` (fin=4 ≤ ini=5) |
| 5 | (X,0,2,10) | (Y,2,4,10) | `false` (fin=2 = ini=2, no hay intersección) |

### `choques`

| Test | asig | Esperado | Razón |
|------|------|----------|-------|
| 1 | (0,1,2,1) | 1 | C2 y C4 en aula 1 pero no solapan; C1 no |
| 2 | (0,0,1,2) | 1 | C1 y C2 en aula 0 y solapan |
| 3 | (0,0,1,0) | 3 | C1-C2, C1-C4, C2-C4 chocan |
| 4 | (1,1,1,1) | 4 | Todos en aula 1; pares solapantes: (C1-C2), (C1-C4), (C2-C3), (C2-C4) |
| 5 | (-1,-1,-1,-1) | 0 | Nadie asignado |

### `capacidadFallida`

| Test | asig | Esperado | Razón |
|------|------|----------|-------|
| 1 | (2,2,2,2) | 0 | A3(50) ≥ todos los cursos |
| 2 | (0,0,1,0) | 2 | A1(25) < C1(30) y A1(25) < C4(35) |
| 3 | (0,0,0,0) | 2 | A1(25) < C1(30) y A1(25) < C4(35) |
| 4 | (1,1,1,1) | 0 | A2(40) ≥ todos |
| 5 | (-1,-1,-1,-1) | 0 | Nadie asignado |

### `desperdicio`

| Test | asig | Esperado | Cálculo |
|------|------|----------|---------|
| 1 | (2,2,2,2) | 100 | (50−30)+(50−20)+(50−15)+(50−35)=20+30+35+15 |
| 2 | (1,1,2,1) | 70 | (40−30)+(40−20)+(50−15)+(40−35)=10+20+35+5 |
| 3 | (1,1,1,1) | 60 | 10+20+25+5 |
| 4 | (0,0,0,0) | 15 | A1(25): 0+(25−20)+(25−15)+0=0+5+10+0 |
| 5 | (-1,-1,-1,-1) | 0 | Nadie asignado |

### `movilidad`

| Test | asig | Esperado | Cálculo |
|------|------|----------|---------|
| 1 | (0,1,2,1) | 12 | Orden ini: C1(0)→A0, C4(1)→A1, C2(2)→A1, C3(5)→A2; dist=5+0+7 |
| 2 | (1,1,1,1) | 0 | Todas en la misma aula, dist=0 |
| 3 | (0,-1,-1,-1) | 0 | Solo 1 curso asignado |
| 4 | (-1,-1,-1,-1) | 0 | Nadie asignado |
| 5 | (2,1,0,2) | ≥0 | Resultado no negativo |

### `generarAsignaciones`

| Test | n | m | Esperado |
|------|---|---|----------|
| 1 | 0 | 3 | 1 ($3^0$) |
| 2 | 1 | 2 | 2 ($2^1$) |
| 3 | 2 | 2 | 4 ($2^2$) |
| 4 | 3 | 2 | 8 ($2^3$) |
| 5 | 2 | 3 | 9 ($3^2$) |

### `asignacionOptima`

| Test | pesos | Condición verificada |
|------|-------|---------------------|
| 1 | (100,100,1,1) | Longitud de asignación = n |
| 2 | (100,100,1,1) | Costo ≥ 0 |
| 3 | (100,100,1,1) | Costo ≤ costo de asignación arbitraria (0,0,0,0) |
| 4 | (1,1,1,1) | Costo ≥ 0 |
| 5 | (50,20,3,2) | Costo ≥ 0 |