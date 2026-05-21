package proyecto

import scala.util.Random

object AsignacionAulas {

  // =========================================================================
  // TIPOS DE DATOS
  // =========================================================================

  type Curso = (String, Int, Int, Int)


  type Cursos = Vector[Curso]


  type Aula = (String, Int)


  type Aulas = Vector[Aula]


  type Asignacion = Vector[Int]


  type Distancias = Vector[Vector[Int]]


  type Pesos = (Int, Int, Int, Int)

  // =========================================================================
  // GENERACION DE ENTRADAS ALEATORIAS
  // =========================================================================


  private val random = new Random()


  def cursosAlAzar(n: Int): Cursos =
    Vector.tabulate(n) { i =>
      val ini = random.nextInt(29)
      val dur = random.nextInt(7) + 2
      ("C" + i, ini, ini + dur, random.nextInt(46) + 5)
    }

  def aulasAlAzar(m: Int): Aulas =
    Vector.tabulate(m)(j => ("E" + j, random.nextInt(46) + 15))

  def distanciasAlAzar(m: Int): Distancias = {
    val v = Vector.fill(m, m)(random.nextInt(m * 2) + 1)
    Vector.tabulate(m, m) { (i, j) =>
      if (i < j) v(i)(j)
      else if (i == j) 0
      else v(j)(i)
    }
  }

  // =========================================================================
  // FUNCIONES DE EXPLORACION
  // =========================================================================

  def idCurso(c: Curso): String  = c._1
  def iniCurso(c: Curso): Int    = c._2
  def finCurso(c: Curso): Int    = c._3
  def estCurso(c: Curso): Int    = c._4

  def idAula(a: Aula): String = a._1
  def capAula(a: Aula): Int   = a._2

  // =========================================================================
  // 2.3 SOLAPAMIENTOS
  // =========================================================================

  def solapan(c1: Curso, c2: Curso): Boolean =
    iniCurso(c1) < finCurso(c2) && iniCurso(c2) < finCurso(c1)

  // =========================================================================
  // 2.4 CHOQUES DE HORARIO
  // =========================================================================

  def choques(cursos: Cursos, a: Asignacion): Int = {
    val n = cursos.length
    (0 until n).toVector.flatMap { i =>
      ((i + 1) until n).toVector.map { j =>
        if (a(i) >= 0 && a(j) >= 0 && a(i) == a(j) && solapan(cursos(i), cursos(j))) 1
        else 0
      }
    }.sum
  }

  // =========================================================================
  // 2.5 CAPACIDAD FALLIDA Y DESPERDICIO
  // =========================================================================

  def capacidadFallida(cursos: Cursos, aulas: Aulas, a: Asignacion): Int =
    cursos.indices.count(i => a(i) >= 0 && capAula(aulas(a(i))) < estCurso(cursos(i)))


  def desperdicio(cursos: Cursos, aulas: Aulas, a: Asignacion): Int =
    cursos.indices
      .filter(i => a(i) >= 0 && capAula(aulas(a(i))) >= estCurso(cursos(i)))
      .map(i => capAula(aulas(a(i))) - estCurso(cursos(i)))
      .sum

  // =========================================================================
  // 2.6 COSTO DE MOVILIDAD
  // =========================================================================

  def movilidad(cursos: Cursos, aulas: Aulas, d: Distancias,
                a: Asignacion): Int = {
    val asignados = cursos.indices
      .filter(i => a(i) >= 0)
      .sortBy(i => iniCurso(cursos(i)))

    if (asignados.length < 2) 0
    else
      asignados
        .sliding(2)
        .map(par => d(a(par(0)))(a(par(1))))
        .sum
  }

  // =========================================================================
  // 2.7 COSTO TOTAL
  // =========================================================================

  def costoAsignacion(cursos: Cursos, aulas: Aulas, d: Distancias,
                      a: Asignacion, w: Pesos): Int = {
    val (wCH, wCF, wDE, wMV) = w
    wCH * choques(cursos, a) +
      wCF * capacidadFallida(cursos, aulas, a) +
      wDE * desperdicio(cursos, aulas, a) +
      wMV * movilidad(cursos, aulas, d, a)
  }

  // =========================================================================
  // 2.8 GENERACION DE ASIGNACIONES
  // =========================================================================

  def generarAsignaciones(n: Int, m: Int): Vector[Asignacion] = {
    if (n == 0)
      Vector(Vector.empty[Int])
    else {
      val sufijos = generarAsignaciones(n - 1, m)
      (0 until m).toVector.flatMap { j =>
        sufijos.map(sufijo => j +: sufijo)
      }
    }
  }

  // =========================================================================
  // 2.9 ASIGNACION OPTIMA
  // =========================================================================

  def asignacionOptima(cursos: Cursos, aulas: Aulas, d: Distancias,
                       w: Pesos): (Asignacion, Int) = {
    val n = cursos.length
    val m = aulas.length
    generarAsignaciones(n, m)
      .map(a => (a, costoAsignacion(cursos, aulas, d, a, w)))
      .minBy(_._2)
  }
}