package proyecto

import org.junit.runner.RunWith
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AsignacionAulasTest extends AnyFunSuite {

  import AsignacionAulas._

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
    Vector(0,5,10),
    Vector(5,0,7),
    Vector(10,7,0)
  )

  // =====================================
  // SOLAPAN (5)
  // =====================================

  test("solapan 1") {
    assert(solapan(cursos(0), cursos(1)))
  }

  test("solapan 2") {
    assert(solapan(cursos(0), cursos(3)))
  }

  test("solapan 3") {
    assert(solapan(cursos(1), cursos(2)))
  }

  test("solapan 4") {
    assert(!solapan(cursos(0), cursos(2)))
  }

  test("solapan 5") {
    val a = ("X",0,2,10)
    val b = ("Y",2,4,10)
    assert(!solapan(a,b))
  }

  // =====================================
  // CHOQUES (5)
  // =====================================

  test("choques 1") {
    assert(choques(cursos, Vector(0,1,2,1)) == 1)
  }

  test("choques 2") {
    assert(choques(cursos, Vector(0,0,1,2)) == 1)
  }

  test("choques 3") {
    assert(choques(cursos, Vector(0,0,1,0)) == 3)
  }

  test("choques 4") {
    assert(choques(cursos, Vector(1,1,1,1)) == 4)
  }

  test("choques 5") {
    assert(choques(cursos, Vector(-1,-1,-1,-1)) == 0)
  }

  // =====================================
  // CAPACIDAD FALLIDA (5)
  // =====================================

  test("capacidad 1") {
    assert(capacidadFallida(cursos,aulas,Vector(2,2,2,2)) == 0)
  }

  test("capacidad 2") {
    assert(capacidadFallida(cursos,aulas,Vector(0,0,1,0)) == 2)
  }

  test("capacidad 3") {
    assert(capacidadFallida(cursos,aulas,Vector(0,0,0,0)) == 2)
  }

  test("capacidad 4") {
    assert(capacidadFallida(cursos,aulas,Vector(1,1,1,1)) == 0)
  }

  test("capacidad 5") {
    assert(capacidadFallida(cursos,aulas,Vector(-1,-1,-1,-1)) == 0)
  }

  // =====================================
  // DESPERDICIO (5)
  // =====================================

  test("desperdicio 1") {
    assert(desperdicio(cursos,aulas,Vector(2,2,2,2)) == 100)
  }

  test("desperdicio 2") {
    assert(desperdicio(cursos,aulas,Vector(1,1,2,1)) == 70)
  }

  test("desperdicio 3") {
    assert(desperdicio(cursos,aulas,Vector(1,1,1,1)) == 60)
  }

  test("desperdicio 4") {
    assert(desperdicio(cursos,aulas,Vector(0,0,0,0)) == 15)
  }

  test("desperdicio 5") {
    assert(desperdicio(cursos,aulas,Vector(-1,-1,-1,-1)) == 0)
  }

  // =====================================
  // MOVILIDAD (5)
  // =====================================

  test("movilidad 1") {
    assert(movilidad(cursos,aulas,dist,Vector(0,1,2,1)) == 12)
  }

  test("movilidad 2") {
    assert(movilidad(cursos,aulas,dist,Vector(1,1,1,1)) == 0)
  }

  test("movilidad 3") {
    assert(movilidad(cursos,aulas,dist,Vector(0,-1,-1,-1)) == 0)
  }

  test("movilidad 4") {
    assert(movilidad(cursos,aulas,dist,Vector(-1,-1,-1,-1)) == 0)
  }

  test("movilidad 5") {
    assert(movilidad(cursos,aulas,dist,Vector(2,1,0,2)) >= 0)
  }

  // =====================================
  // COSTO ASIGNACION (5)
  // =====================================

  test("costo 1") {
    val asig = Vector(0,0,1,0)

    val esperado =
      10 * choques(cursos,asig) +
        20 * capacidadFallida(cursos,aulas,asig) +
        desperdicio(cursos,aulas,asig) +
        movilidad(cursos,aulas,dist,asig)

    assert(
      costoAsignacion(
        cursos,aulas,dist,
        asig,(10,20,1,1)
      ) == esperado
    )
  }

  test("costo 2") {
    assert(
      costoAsignacion(
        cursos,aulas,dist,
        Vector(2,2,2,2),
        (1,1,1,1)
      ) >= 0
    )
  }

  test("costo 3") {
    assert(
      costoAsignacion(
        cursos,aulas,dist,
        Vector(1,1,1,1),
        (5,5,5,5)
      ) >= 0
    )
  }

  test("costo 4") {
    assert(
      costoAsignacion(
        cursos,aulas,dist,
        Vector(-1,-1,-1,-1),
        (1,1,1,1)
      ) == 0
    )
  }

  test("costo 5") {
    assert(
      costoAsignacion(
        cursos,aulas,dist,
        Vector(0,1,2,1),
        (10,10,10,10)
      ) >= 0
    )
  }

  // =====================================
  // GENERAR ASIGNACIONES (5)
  // =====================================

  test("generar 1") {
    assert(generarAsignaciones(0,3).size == 1)
  }

  test("generar 2") {
    assert(generarAsignaciones(1,2).size == 2)
  }

  test("generar 3") {
    assert(generarAsignaciones(2,2).size == 4)
  }

  test("generar 4") {
    assert(generarAsignaciones(3,2).size == 8)
  }

  test("generar 5") {
    assert(generarAsignaciones(2,3).size == 9)
  }

  // =====================================
  // ASIGNACION OPTIMA (5)
  // =====================================

  test("optima 1") {
    val (a,_) =
      asignacionOptima(cursos,aulas,dist,(100,100,1,1))

    assert(a.length == cursos.length)
  }

  test("optima 2") {
    val (_,costo) =
      asignacionOptima(cursos,aulas,dist,(100,100,1,1))

    assert(costo >= 0)
  }

  test("optima 3") {

    val costoArbitrario =
      costoAsignacion(
        cursos,aulas,dist,
        Vector(0,0,0,0),
        (100,100,1,1)
      )

    val (_,costoOptimo) =
      asignacionOptima(
        cursos,aulas,dist,
        (100,100,1,1)
      )

    assert(costoOptimo <= costoArbitrario)
  }

  test("optima 4") {
    val (_,costo) =
      asignacionOptima(cursos,aulas,dist,(1,1,1,1))

    assert(costo >= 0)
  }

  test("optima 5") {
    val (_,costo) =
      asignacionOptima(cursos,aulas,dist,(50,20,3,2))

    assert(costo >= 0)
  }
}