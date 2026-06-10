package proyecto

import org.junit.runner.RunWith
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AsignacionAulasParTest extends AnyFunSuite {

  import AsignacionAulas._
  import AsignacionAulasPar._

  val cursos: Cursos = Vector(
    ("C1", 0, 4, 30),
    ("C2", 2, 6, 20),
    ("C3", 5, 8, 15),
    ("C4", 1, 3, 35)
  )

  val aulas: Aulas = Vector(
    ("A1", 25),
    ("A2", 40),
    ("A3", 50)
  )

  val dist: Distancias = Vector(
    Vector(0, 5, 10),
    Vector(5, 0, 7),
    Vector(10, 7, 0)
  )

  // =====================================
  // CHOQUES PAR (5)
  // =====================================

  test("choquesPar coincide caso 1") {
    val asig = Vector(0,1,2,1)
    assert(choquesPar(cursos,asig) ==
      choques(cursos,asig))
  }

  test("choquesPar coincide caso 2") {
    val asig = Vector(0,0,1,2)
    assert(choquesPar(cursos,asig) ==
      choques(cursos,asig))
  }

  test("choquesPar coincide caso 3") {
    val asig = Vector(0,0,1,0)
    assert(choquesPar(cursos,asig) ==
      choques(cursos,asig))
  }

  test("choquesPar coincide caso 4") {
    val asig = Vector(1,1,1,1)
    assert(choquesPar(cursos,asig) ==
      choques(cursos,asig))
  }

  test("choquesPar coincide caso 5") {
    val asig = Vector(-1,-1,-1,-1)
    assert(choquesPar(cursos,asig) ==
      choques(cursos,asig))
  }

  // =====================================
  // DESPERDICIO PAR (5)
  // =====================================

  test("desperdicioPar coincide caso 1") {
    val asig = Vector(2,2,2,2)
    assert(desperdicioPar(cursos,aulas,asig) ==
      desperdicio(cursos,aulas,asig))
  }

  test("desperdicioPar coincide caso 2") {
    val asig = Vector(1,1,2,1)
    assert(desperdicioPar(cursos,aulas,asig) ==
      desperdicio(cursos,aulas,asig))
  }

  test("desperdicioPar coincide caso 3") {
    val asig = Vector(1,1,1,1)
    assert(desperdicioPar(cursos,aulas,asig) ==
      desperdicio(cursos,aulas,asig))
  }

  test("desperdicioPar coincide caso 4") {
    val asig = Vector(0,0,0,0)
    assert(desperdicioPar(cursos,aulas,asig) ==
      desperdicio(cursos,aulas,asig))
  }

  test("desperdicioPar coincide caso 5") {
    val asig = Vector(-1,-1,-1,-1)
    assert(desperdicioPar(cursos,aulas,asig) ==
      desperdicio(cursos,aulas,asig))
  }

  // =====================================
  // MOVILIDAD PAR (5)
  // =====================================

  test("movilidadPar coincide caso 1") {
    val asig = Vector(0,1,2,1)
    assert(movilidadPar(cursos,aulas,dist,asig) ==
      movilidad(cursos,aulas,dist,asig))
  }

  test("movilidadPar coincide caso 2") {
    val asig = Vector(2,1,0,2)
    assert(movilidadPar(cursos,aulas,dist,asig) ==
      movilidad(cursos,aulas,dist,asig))
  }

  test("movilidadPar coincide caso 3") {
    val asig = Vector(1,1,1,1)
    assert(movilidadPar(cursos,aulas,dist,asig) ==
      movilidad(cursos,aulas,dist,asig))
  }

  test("movilidadPar coincide caso 4") {
    val asig = Vector(0,-1,-1,-1)
    assert(movilidadPar(cursos,aulas,dist,asig) ==
      movilidad(cursos,aulas,dist,asig))
  }

  test("movilidadPar coincide caso 5") {
    val asig = Vector(-1,-1,-1,-1)
    assert(movilidadPar(cursos,aulas,dist,asig) ==
      movilidad(cursos,aulas,dist,asig))
  }

  // =====================================
  // GENERAR ASIGNACIONES PAR (5)
  // =====================================

  test("generarAsignacionesPar coincide 0 cursos") {
    assert(
      generarAsignacionesPar(0,3).size ==
        generarAsignaciones(0,3).size
    )
  }

  test("generarAsignacionesPar coincide 1 curso") {
    assert(
      generarAsignacionesPar(1,2).size ==
        generarAsignaciones(1,2).size
    )
  }

  test("generarAsignacionesPar coincide 2 cursos") {
    assert(
      generarAsignacionesPar(2,2).size ==
        generarAsignaciones(2,2).size
    )
  }

  test("generarAsignacionesPar coincide 3 cursos") {
    assert(
      generarAsignacionesPar(3,2).size ==
        generarAsignaciones(3,2).size
    )
  }

  test("generarAsignacionesPar coincide 2 cursos 3 aulas") {
    assert(
      generarAsignacionesPar(2,3).size ==
        generarAsignaciones(2,3).size
    )
  }

  // =====================================
  // ASIGNACION OPTIMA PAR (5)
  // =====================================

  test("asignacionOptimaPar coincide pesos altos") {

    val sec =
      asignacionOptima(
        cursos,aulas,dist,
        (100,100,1,1)
      )

    val par =
      asignacionOptimaPar(
        cursos,aulas,dist,
        (100,100,1,1)
      )

    assert(sec._2 == par._2)
  }

  test("asignacionOptimaPar coincide pesos medios") {

    val sec =
      asignacionOptima(
        cursos,aulas,dist,
        (10,10,1,1)
      )

    val par =
      asignacionOptimaPar(
        cursos,aulas,dist,
        (10,10,1,1)
      )

    assert(sec._2 == par._2)
  }

  test("asignacionOptimaPar coincide pesos bajos") {

    val sec =
      asignacionOptima(
        cursos,aulas,dist,
        (1,1,1,1)
      )

    val par =
      asignacionOptimaPar(
        cursos,aulas,dist,
        (1,1,1,1)
      )

    assert(sec._2 == par._2)
  }

  test("asignacionOptimaPar coincide configuracion personalizada 1") {

    val sec =
      asignacionOptima(
        cursos,aulas,dist,
        (50,20,3,2)
      )

    val par =
      asignacionOptimaPar(
        cursos,aulas,dist,
        (50,20,3,2)
      )

    assert(sec._2 == par._2)
  }

  test("asignacionOptimaPar coincide configuracion personalizada 2") {

    val sec =
      asignacionOptima(
        cursos,aulas,dist,
        (200,100,5,5)
      )

    val par =
      asignacionOptimaPar(
        cursos,aulas,dist,
        (200,100,5,5)
      )

    assert(sec._2 == par._2)
  }
}