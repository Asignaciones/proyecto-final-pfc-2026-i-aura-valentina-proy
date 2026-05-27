package proyecto

import org.scalatest.funsuite.AnyFunSuite
import proyecto.AsignacionAulas._
import proyecto.AsignacionAulasPar._

import org.scalatestplus.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class AsignacionAulasParTest extends AnyFunSuite {

  // =========================================================================
  // DATOS DE PRUEBA
  // =========================================================================

  val cursosEj1: Cursos = Vector(
    ("M01", 4,  8,  25),
    ("M02", 6,  10, 30),
    ("M03", 12, 16, 20)
  )
  val aulasEj1: Aulas = Vector(("E101", 30), ("E102", 40))
  val distEj1: Distancias = Vector(Vector(0, 3), Vector(3, 0))
  val pesos: Pesos = (1000, 100, 1, 2)

  val cursosEj2: Cursos = Vector(
    ("F01", 0,  4,  40),
    ("F02", 4,  8,  25),
    ("F03", 8,  12, 50),
    ("F04", 12, 16, 15)
  )
  val aulasEj2: Aulas = Vector(("S201", 45), ("S202", 30))
  val distEj2: Distancias = Vector(Vector(0, 5), Vector(5, 0))


  val cursosMed: Cursos = Vector.tabulate(8)(i =>
    ("C" + i, i * 3, i * 3 + 2, 10 + i * 3)
  )
  val aulasMed: Aulas  = Vector(("E1", 40), ("E2", 50))
  val distMed: Distancias = Vector(Vector(0, 4), Vector(4, 0))

  // =========================================================================
  // TESTS: choquesPar
  // =========================================================================

  test("choquesPar - ej1 asig1: mismo resultado que secuencial (=1)") {
    val a = Vector(0, 0, 1)
    assert(choquesPar(cursosEj1, a) == choques(cursosEj1, a))
  }

  test("choquesPar - ej1 asig2: mismo resultado que secuencial (=0)") {
    val a = Vector(0, 1, 0)
    assert(choquesPar(cursosEj1, a) == choques(cursosEj1, a))
  }

  test("choquesPar - tres cursos solapados en misma aula: 3 choques") {
    val cursos = Vector(("A", 0, 6, 20), ("B", 2, 8, 20), ("C", 4, 10, 20))
    assert(choquesPar(cursos, Vector(0, 0, 0)) == 3)
  }

  test("choquesPar - sin solapamiento: 0 choques") {
    val cursos = Vector(("A", 0, 4, 20), ("B", 5, 9, 20), ("C", 10, 14, 20))
    assert(choquesPar(cursos, Vector(0, 0, 0)) == 0)
  }

  test("choquesPar - consistente con secuencial en ej2") {
    val a = Vector(0, 1, 0, 1)
    assert(choquesPar(cursosEj2, a) == choques(cursosEj2, a))
  }

  // =========================================================================
  // TESTS: desperdicioPar
  // =========================================================================

  test("desperdicioPar - ej1 asig2: mismo resultado que secuencial (=25)") {
    val a = Vector(0, 1, 0)
    assert(desperdicioPar(cursosEj1, aulasEj1, a) == desperdicio(cursosEj1, aulasEj1, a))
  }

  test("desperdicioPar - ej2 asig1: mismo resultado que secuencial (=25)") {
    val a = Vector(0, 1, 0, 1)
    assert(desperdicioPar(cursosEj2, aulasEj2, a) == desperdicio(cursosEj2, aulasEj2, a))
  }

  test("desperdicioPar - aula exacta: 0 desperdicio") {
    val cursos = Vector(("A", 0, 4, 30), ("B", 5, 9, 30))
    val aulas  = Vector(("E1", 30))
    assert(desperdicioPar(cursos, aulas, Vector(0, 0)) == 0)
  }

  test("desperdicioPar - capacidad fallida no aporta al desperdicio") {
    val cursos = Vector(("A", 0, 4, 50), ("B", 5, 9, 10))
    val aulas  = Vector(("E1", 20))
    assert(desperdicioPar(cursos, aulas, Vector(0, 0)) ==
      desperdicio(cursos, aulas, Vector(0, 0)))
  }

  test("desperdicioPar - vector grande: consistente con secuencial") {
    val a = Vector(0, 1, 0, 1, 0, 1, 0, 1)
    assert(desperdicioPar(cursosMed, aulasMed, a) ==
      desperdicio(cursosMed, aulasMed, a))
  }

  // =========================================================================
  // TESTS: movilidadPar
  // =========================================================================

  test("movilidadPar - ej1 asig1: mismo resultado que secuencial (=3)") {
    val a = Vector(0, 0, 1)
    assert(movilidadPar(cursosEj1, aulasEj1, distEj1, a) ==
      movilidad(cursosEj1, aulasEj1, distEj1, a))
  }

  test("movilidadPar - ej1 asig2: mismo resultado que secuencial (=6)") {
    val a = Vector(0, 1, 0)
    assert(movilidadPar(cursosEj1, aulasEj1, distEj1, a) ==
      movilidad(cursosEj1, aulasEj1, distEj1, a))
  }

  test("movilidadPar - ej2 asig1: mismo resultado que secuencial (=15)") {
    val a = Vector(0, 1, 0, 1)
    assert(movilidadPar(cursosEj2, aulasEj2, distEj2, a) ==
      movilidad(cursosEj2, aulasEj2, distEj2, a))
  }

  test("movilidadPar - un solo curso: 0") {
    val cursos = Vector(("A", 0, 4, 20))
    val aulas  = Vector(("E1", 30))
    val dist   = Vector(Vector(0))
    assert(movilidadPar(cursos, aulas, dist, Vector(0)) == 0)
  }

  test("movilidadPar - vector grande: consistente con secuencial") {
    val a = Vector(0, 1, 0, 1, 0, 1, 0, 1)
    assert(movilidadPar(cursosMed, aulasMed, distMed, a) ==
      movilidad(cursosMed, aulasMed, distMed, a))
  }

  // =========================================================================
  // TESTS: generarAsignacionesPar
  // =========================================================================

  test("generarAsignacionesPar - n=0: una asignacion vacia") {
    assert(generarAsignacionesPar(0, 3) == Vector(Vector()))
  }

  test("generarAsignacionesPar - n=1, m=2: mismo conjunto que secuencial") {
    assert(generarAsignacionesPar(1, 2).toSet == generarAsignaciones(1, 2).toSet)
  }

  test("generarAsignacionesPar - n=2, m=2: 4 asignaciones correctas") {
    assert(generarAsignacionesPar(2, 2).toSet == generarAsignaciones(2, 2).toSet)
  }

  test("generarAsignacionesPar - tamano correcto m^n: n=3, m=3") {
    assert(generarAsignacionesPar(3, 3).length == 27)
  }

  test("generarAsignacionesPar - mismo conjunto que secuencial: n=3, m=3") {
    assert(generarAsignacionesPar(3, 3).toSet == generarAsignaciones(3, 3).toSet)
  }

  // =========================================================================
  // TESTS: asignacionOptimaPar
  // =========================================================================

  test("asignacionOptimaPar - mismo costo que secuencial: ej1") {
    val (_, costoSec) = asignacionOptima(cursosEj1, aulasEj1, distEj1, pesos)
    val (_, costoPar) = asignacionOptimaPar(cursosEj1, aulasEj1, distEj1, pesos)
    assert(costoSec == costoPar)
  }

  test("asignacionOptimaPar - mismo costo que secuencial: ej2") {
    val (_, costoSec) = asignacionOptima(cursosEj2, aulasEj2, distEj2, pesos)
    val (_, costoPar) = asignacionOptimaPar(cursosEj2, aulasEj2, distEj2, pesos)
    assert(costoSec == costoPar)
  }

  test("asignacionOptimaPar - la asignacion retornada tiene el costo declarado") {
    val (a, costo) = asignacionOptimaPar(cursosEj1, aulasEj1, distEj1, pesos)
    assert(costoAsignacion(cursosEj1, aulasEj1, distEj1, a, pesos) == costo)
  }

  test("asignacionOptimaPar - optima es minima global sobre todas las asignaciones") {
    val (_, costoPar) = asignacionOptimaPar(cursosEj1, aulasEj1, distEj1, pesos)
    val minReal = generarAsignaciones(cursosEj1.length, aulasEj1.length)
      .map(a => costoAsignacion(cursosEj1, aulasEj1, distEj1, a, pesos))
      .min
    assert(costoPar == minReal)
  }

  test("asignacionOptimaPar - un curso, un aula: unica asignacion") {
    val cursos = Vector(("A", 0, 4, 20))
    val aulas  = Vector(("E1", 30))
    val dist   = Vector(Vector(0))
    val (a, _) = asignacionOptimaPar(cursos, aulas, dist, pesos)
    assert(a == Vector(0))
  }
}