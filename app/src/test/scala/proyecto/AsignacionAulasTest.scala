package proyecto

import org.scalatest.funsuite.AnyFunSuite
import proyecto.AsignacionAulas._

import org.scalatestplus.junit.JUnitRunner
import org.junit.runner.RunWith
@RunWith(classOf[JUnitRunner])
class AsignacionAulasTest extends AnyFunSuite {

  // =========================================================================
  // DATOS DE PRUEBA — Ejemplos del enunciado
  // =========================================================================

  val cursosEj1: Cursos = Vector(
    ("M01", 4,  8,  25),
    ("M02", 6,  10, 30),
    ("M03", 12, 16, 20)
  )
  val aulasEj1: Aulas = Vector(
    ("E101", 30),
    ("E102", 40)
  )
  val distEj1: Distancias = Vector(
    Vector(0, 3),
    Vector(3, 0)
  )
  val pesos: Pesos = (1000, 100, 1, 2)

  val cursosEj2: Cursos = Vector(
    ("F01", 0,  4,  40),
    ("F02", 4,  8,  25),
    ("F03", 8,  12, 50),
    ("F04", 12, 16, 15)
  )
  val aulasEj2: Aulas = Vector(
    ("S201", 45),
    ("S202", 30)
  )
  val distEj2: Distancias = Vector(
    Vector(0, 5),
    Vector(5, 0)
  )

  // =========================================================================
  // TESTS: solapan
  // =========================================================================

  test("solapan - solapamiento parcial: M01[4,8) y M02[6,10)") {
    assert(solapan(("M01", 4, 8, 25), ("M02", 6, 10, 30)))
  }

  test("solapan - consecutivos exactos no solapan: [4,8) y [8,12)") {
    assert(!solapan(("A", 4, 8, 20), ("B", 8, 12, 20)))
  }

  test("solapan - hueco entre cursos: [4,8) y [10,14)") {
    assert(!solapan(("A", 4, 8, 20), ("B", 10, 14, 20)))
  }

  test("solapan - contencion completa: [2,12) contiene a [4,8)") {
    assert(solapan(("A", 2, 12, 30), ("B", 4, 8, 20)))
  }

  test("solapan - mismo intervalo exacto") {
    assert(solapan(("A", 4, 8, 20), ("B", 4, 8, 25)))
  }

  test("solapan - simetria: solapan(c1,c2) == solapan(c2,c1)") {
    val c1 = ("A", 4, 8,  20)
    val c2 = ("B", 6, 10, 25)
    assert(solapan(c1, c2) == solapan(c2, c1))
  }

  // =========================================================================
  // TESTS: choques
  // =========================================================================

  test("choques - asignacion 1 ej1: M01 y M02 en E101 -> 1 choque") {
    // alpha = [0,0,1]: M01->E101, M02->E101 (solapan), M03->E102
    assert(choques(cursosEj1, Vector(0, 0, 1)) == 1)
  }

  test("choques - asignacion 2 ej1: sin choques") {
    // alpha = [0,1,0]: cada par en aula distinta o no solapan
    assert(choques(cursosEj1, Vector(0, 1, 0)) == 0)
  }

  test("choques - todos en aulas distintas: 0 choques") {
    val cursos = Vector(("A", 0, 4, 20), ("B", 0, 4, 20), ("C", 0, 4, 20))
    assert(choques(cursos, Vector(0, 1, 2)) == 0)
  }

  test("choques - tres cursos solapados en misma aula: 3 choques") {
    // Pares (A,B), (A,C), (B,C) -> 3
    val cursos = Vector(("A", 0, 6, 20), ("B", 2, 8, 20), ("C", 4, 10, 20))
    assert(choques(cursos, Vector(0, 0, 0)) == 3)
  }

  test("choques - misma aula pero NO solapan (consecutivos): 0 choques") {
    val cursos = Vector(("A", 0, 4, 20), ("B", 4, 8, 20))
    assert(choques(cursos, Vector(0, 0)) == 0)
  }

  // =========================================================================
  // TESTS: capacidadFallida
  // =========================================================================

  test("capacidadFallida - ej1 asig2: todas las aulas alcanzan -> 0") {
    // E101(30)>=M01(25), E102(40)>=M02(30), E101(30)>=M03(20)
    assert(capacidadFallida(cursosEj1, aulasEj1, Vector(0, 1, 0)) == 0)
  }

  test("capacidadFallida - ej2 asig1: F03(50) no cabe en S201(45) -> 1") {
    assert(capacidadFallida(cursosEj2, aulasEj2, Vector(0, 1, 0, 1)) == 1)
  }

  test("capacidadFallida - capacidad exactamente igual: no falla") {
    val cursos = Vector(("A", 0, 4, 30))
    val aulas  = Vector(("E1", 30))
    assert(capacidadFallida(cursos, aulas, Vector(0)) == 0)
  }

  test("capacidadFallida - todos los cursos fallan") {
    val cursos = Vector(("A", 0, 4, 50), ("B", 4, 8, 50))
    val aulas  = Vector(("E1", 20))
    assert(capacidadFallida(cursos, aulas, Vector(0, 0)) == 2)
  }

  test("capacidadFallida - ej2 asig2: F03 tampoco cabe en S202 -> 1") {
    assert(capacidadFallida(cursosEj2, aulasEj2, Vector(0, 1, 1, 0)) == 1)
  }

  // =========================================================================
  // TESTS: desperdicio
  // =========================================================================

  test("desperdicio - ej1 asig2: DE = (30-25)+(40-30)+(30-20) = 25") {
    assert(desperdicio(cursosEj1, aulasEj1, Vector(0, 1, 0)) == 25)
  }

  test("desperdicio - ej2 asig1: DE = (45-40)+(30-25)+0+(30-15) = 25") {
    // F03 falla capacidad -> no cuenta en desperdicio
    assert(desperdicio(cursosEj2, aulasEj2, Vector(0, 1, 0, 1)) == 25)
  }

  test("desperdicio - capacidad exacta: 0") {
    val cursos = Vector(("A", 0, 4, 30))
    val aulas  = Vector(("E1", 30))
    assert(desperdicio(cursos, aulas, Vector(0)) == 0)
  }

  test("desperdicio - capacidad fallida no aporta al desperdicio") {
    val cursos = Vector(("A", 0, 4, 50))
    val aulas  = Vector(("E1", 20))
    assert(desperdicio(cursos, aulas, Vector(0)) == 0)
  }

  test("desperdicio - varios cursos con exceso variado") {
    // (40-10)+(40-20)+(40-5) = 30+20+35 = 85
    val cursos = Vector(("A", 0, 4, 10), ("B", 4, 8, 20), ("C", 8, 12, 5))
    val aulas  = Vector(("E1", 40))
    assert(desperdicio(cursos, aulas, Vector(0, 0, 0)) == 85)
  }

  // =========================================================================
  // TESTS: movilidad
  // =========================================================================

  test("movilidad - ej1 asig1: orden M01,M02,M03 -> d(0,0)+d(0,1) = 0+3 = 3") {
    assert(movilidad(cursosEj1, aulasEj1, distEj1, Vector(0, 0, 1)) == 3)
  }

  test("movilidad - ej1 asig2: orden M01,M02,M03 -> d(0,1)+d(1,0) = 3+3 = 6") {
    assert(movilidad(cursosEj1, aulasEj1, distEj1, Vector(0, 1, 0)) == 6)
  }

  test("movilidad - ej2 asig1: d(0,1)+d(1,0)+d(0,1) = 5+5+5 = 15") {
    assert(movilidad(cursosEj2, aulasEj2, distEj2, Vector(0, 1, 0, 1)) == 15)
  }

  test("movilidad - un solo curso: MV = 0") {
    val cursos = Vector(("A", 0, 4, 20))
    val aulas  = Vector(("E1", 30))
    val dist   = Vector(Vector(0))
    assert(movilidad(cursos, aulas, dist, Vector(0)) == 0)
  }

  test("movilidad - todos en la misma aula: distancia diagonal 0") {
    val cursos = Vector(("A", 0, 4, 20), ("B", 5, 9, 20), ("C", 10, 14, 20))
    val aulas  = Vector(("E1", 30), ("E2", 30))
    val dist   = Vector(Vector(0, 5), Vector(5, 0))
    assert(movilidad(cursos, aulas, dist, Vector(0, 0, 0)) == 0)
  }

  // =========================================================================
  // TESTS: costoAsignacion
  // =========================================================================

  test("costoAsignacion - ej1 asig1: CH=1,CF=0,DE=25,MV=3 -> CT=1031") {
    assert(costoAsignacion(cursosEj1, aulasEj1, distEj1, Vector(0, 0, 1), pesos) == 1031)
  }

  test("costoAsignacion - ej1 asig2: CH=0,CF=0,DE=25,MV=6 -> CT=37") {
    assert(costoAsignacion(cursosEj1, aulasEj1, distEj1, Vector(0, 1, 0), pesos) == 37)
  }

  test("costoAsignacion - ej2 asig1: CT = 0+100+25+30 = 155") {
    assert(costoAsignacion(cursosEj2, aulasEj2, distEj2, Vector(0, 1, 0, 1), pesos) == 155)
  }

  test("costoAsignacion - ej2 asig2: CT = 0+100+40+20 = 160") {
    assert(costoAsignacion(cursosEj2, aulasEj2, distEj2, Vector(0, 1, 1, 0), pesos) == 160)
  }

  test("costoAsignacion - pesos todos cero: CT = 0 siempre") {
    assert(costoAsignacion(cursosEj1, aulasEj1, distEj1, Vector(0, 0, 1), (0,0,0,0)) == 0)
  }

  // =========================================================================
  // TESTS: generarAsignaciones
  // =========================================================================

  test("generarAsignaciones - n=0: una sola asignacion vacia") {
    assert(generarAsignaciones(0, 3) == Vector(Vector()))
  }

  test("generarAsignaciones - n=1, m=2: exactamente Vector(0) y Vector(1)") {
    assert(generarAsignaciones(1, 2).toSet == Set(Vector(0), Vector(1)))
  }

  test("generarAsignaciones - n=2, m=2: 4 asignaciones correctas") {
    assert(generarAsignaciones(2, 2).toSet == Set(
      Vector(0,0), Vector(0,1), Vector(1,0), Vector(1,1)
    ))
  }

  test("generarAsignaciones - tamano exacto m^n") {
    assert(generarAsignaciones(3, 2).length == 8)
    assert(generarAsignaciones(2, 3).length == 9)
    assert(generarAsignaciones(4, 2).length == 16)
  }

  test("generarAsignaciones - todas las asignaciones tienen longitud n") {
    generarAsignaciones(3, 2).foreach(a => assert(a.length == 3))
  }

  test("generarAsignaciones - todos los valores en {0,...,m-1}") {
    val m = 3
    generarAsignaciones(3, m).foreach { a =>
      a.foreach(v => assert(v >= 0 && v < m))
    }
  }

  // =========================================================================
  // TESTS: asignacionOptima
  // =========================================================================

  test("asignacionOptima - costo retornado coincide con costoAsignacion") {
    val (a, costo) = asignacionOptima(cursosEj1, aulasEj1, distEj1, pesos)
    assert(costoAsignacion(cursosEj1, aulasEj1, distEj1, a, pesos) == costo)
  }

  test("asignacionOptima - la optima no es peor que ninguna otra asignacion") {
    val (_, costoOpt) = asignacionOptima(cursosEj1, aulasEj1, distEj1, pesos)
    val minReal = generarAsignaciones(cursosEj1.length, aulasEj1.length)
      .map(a => costoAsignacion(cursosEj1, aulasEj1, distEj1, a, pesos))
      .min
    assert(costoOpt == minReal)
  }

  test("asignacionOptima - ej1: CT optimo <= 37 (sabemos que [0,1,0] da 37)") {
    val (_, costo) = asignacionOptima(cursosEj1, aulasEj1, distEj1, pesos)
    assert(costo <= 37)
  }

  test("asignacionOptima - ej2: CF >= 1 en toda asignacion (F03 no cabe)") {
    val (a, _) = asignacionOptima(cursosEj2, aulasEj2, distEj2, pesos)
    assert(capacidadFallida(cursosEj2, aulasEj2, a) >= 1)
  }

  test("asignacionOptima - un curso, un aula: unica asignacion posible") {
    val cursos = Vector(("A", 0, 4, 20))
    val aulas  = Vector(("E1", 30))
    val dist   = Vector(Vector(0))
    val (a, _) = asignacionOptima(cursos, aulas, dist, pesos)
    assert(a == Vector(0))
  }
}