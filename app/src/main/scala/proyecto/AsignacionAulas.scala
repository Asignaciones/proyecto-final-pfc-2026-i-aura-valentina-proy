package proyecto

import scala.util.Random
import scala.annotation.tailrec

object AsignacionAulas {

  // SOLUCIÓN AL ERROR EN ROJO: Los tipos ahora viven dentro del object para Scala 2
  type Curso      = (String, Int, Int, Int)
  type Cursos     = Vector[Curso]
  type Aula       = (String, Int)
  type Aulas      = Vector[Aula]
  type Asignacion = Vector[Int]
  type Distancias = Vector[Vector[Int]]
  type Pesos      = (Int, Int, Int, Int)

  val rng = new Random()

  // ----------------------------------------------------------
  // 2.1  Generación aleatoria de entradas
  // ----------------------------------------------------------

  def cursosAlAzar(n: Int): Cursos = {
    val indices = (0 until n).toVector
    indices.map { k =>
      val bloqueInicio = rng.nextInt(29)
      val cuantosDura  = 2 + rng.nextInt(7)
      val matriculados = 5 + rng.nextInt(46)
      ("C" + k, bloqueInicio, bloqueInicio + cuantosDura, matriculados)
    }
  }

  def aulasAlAzar(m: Int): Aulas = {
    val indices = (0 until m).toVector
    indices.map(k => ("E" + k, 15 + rng.nextInt(46)))
  }

  def distanciasAlAzar(m: Int): Distancias = {
    val mitadSuperior = Vector.fill(m, m)(1 + rng.nextInt(m * 2))
    Vector.tabulate(m, m) { (fila, col) =>
      if (fila == col)     0
      else if (fila < col) mitadSuperior(fila)(col)
      else                 mitadSuperior(col)(fila)
    }
  }

  // ----------------------------------------------------------
  // 2.2  Getters
  // ----------------------------------------------------------

  def idCurso(c: Curso): String  = c._1
  def iniCurso(c: Curso): Int    = c._2
  def finCurso(c: Curso): Int    = c._3
  def estCurso(c: Curso): Int    = c._4

  def idAula(a: Aula): String = a._1
  def capAula(a: Aula): Int   = a._2

  // ----------------------------------------------------------
  // 2.3  Solapan
  // ----------------------------------------------------------

  def solapan(c1: Curso, c2: Curso): Boolean = {
    val c1EmpiezaAntesDeQueFinalice2 = iniCurso(c1) < finCurso(c2)
    val c2EmpiezaAntesDeQueFinalice1 = iniCurso(c2) < finCurso(c1)
    c1EmpiezaAntesDeQueFinalice2 && c2EmpiezaAntesDeQueFinalice1
  }

  // ----------------------------------------------------------
  // 2.4  Choques
  // ----------------------------------------------------------

  def choques(cursos: Cursos, asig: Asignacion): Int = {
    @tailrec // Optimizada y anotada para evitar StackOverflow
    def recorrer(i: Int, j: Int, acum: Int): Int = {
      if (i >= cursos.length) acum
      else if (j >= cursos.length) recorrer(i + 1, i + 2, acum)
      else {
        val hayChoque =
          asig(i) >= 0 && asig(j) >= 0 &&
            asig(i) == asig(j) &&
            solapan(cursos(i), cursos(j))
        recorrer(i, j + 1, if (hayChoque) acum + 1 else acum)
      }
    }
    recorrer(0, 1, 0)
  }

  // ----------------------------------------------------------
  // 2.5  Capacidad Fallida y Desperdicio
  // ----------------------------------------------------------

  def capacidadFallida(cursos: Cursos, aulas: Aulas, asig: Asignacion): Int = {
    @tailrec // Optimizada y anotada
    def ir(i: Int, acum: Int): Int = {
      if (i >= cursos.length) acum
      else {
        val falla = asig(i) >= 0 && capAula(aulas(asig(i))) < estCurso(cursos(i))
        ir(i + 1, if (falla) acum + 1 else acum)
      }
    }
    ir(0, 0)
  }

  def desperdicio(cursos: Cursos, aulas: Aulas, asig: Asignacion): Int = {
    @tailrec // Optimizada y anotada
    def ir(i: Int, acum: Int): Int = {
      if (i >= cursos.length) acum
      else {
        val sobra =
          if (asig(i) >= 0) {
            val diff = capAula(aulas(asig(i))) - estCurso(cursos(i))
            if (diff > 0) diff else 0
          } else 0
        ir(i + 1, acum + sobra)
      }
    }
    ir(0, 0)
  }

  // ----------------------------------------------------------
  // 2.6  Movilidad
  // ----------------------------------------------------------

  def movilidad(cursos: Cursos, aulas: Aulas, dist: Distancias, asig: Asignacion): Int = {
    val soloAsignados = cursos.indices.filter(asig(_) >= 0).toVector
    val porHorario    = soloAsignados.sortBy(i => iniCurso(cursos(i)))

    @tailrec // Optimizada y anotada
    def sumar(pos: Int, acum: Int): Int = {
      if (pos + 1 >= porHorario.length) acum
      else sumar(pos + 1, acum + dist(asig(porHorario(pos)))(asig(porHorario(pos + 1))))
    }

    if (porHorario.length <= 1) 0
    else sumar(0, 0)
  }

  // ----------------------------------------------------------
  // 2.7  Costo Asignacion
  // ----------------------------------------------------------

  def costoAsignacion(cursos: Cursos, aulas: Aulas, dist: Distancias,
                      asig: Asignacion, pesos: Pesos): Int = {
    val (wCH, wCF, wDE, wMV) = pesos
    val penalizacionChoques     = wCH * choques(cursos, asig)
    val penalizacionCapacidad   = wCF * capacidadFallida(cursos, aulas, asig)
    val penalizacionDesperdicio = wDE * desperdicio(cursos, aulas, asig)
    val penalizacionMovilidad   = wMV * movilidad(cursos, aulas, dist, asig)
    penalizacionChoques + penalizacionCapacidad + penalizacionDesperdicio + penalizacionMovilidad
  }

  // ----------------------------------------------------------
  // 2.8  Generando asignaciones
  // ----------------------------------------------------------

  def generarAsignaciones(n: Int, m: Int): Vector[Asignacion] = {
    if (n == 0) {
      Vector(Vector.empty[Int])
    } else {
      val aulasPosibles   = (0 until m).toVector
      val subAsignaciones = generarAsignaciones(n - 1, m)
      aulasPosibles.flatMap { aulaParaCursoN =>
        subAsignaciones.map(sub => aulaParaCursoN +: sub)
      }
    }
  }

  // ----------------------------------------------------------
  // 2.9  Cálculo de Asignación Óptima
  // ----------------------------------------------------------

  def asignacionOptima(cursos: Cursos, aulas: Aulas, dist: Distancias,
                       pesos: Pesos): (Asignacion, Int) = {
    val candidatas = generarAsignaciones(cursos.length, aulas.length)

    @tailrec // Optimizada y anotada
    def buscarMejor(resto: Vector[Asignacion], mejorHasta: Asignacion, costoMin: Int): (Asignacion, Int) = {
      if (resto.isEmpty) (mejorHasta, costoMin)
      else {
        val c = costoAsignacion(cursos, aulas, dist, resto.head, pesos)
        if (c < costoMin) buscarMejor(resto.tail, resto.head, c)
        else              buscarMejor(resto.tail, mejorHasta, costoMin)
      }
    }

    if (candidatas.isEmpty) (Vector.empty, 0)
    else {
      val costoPrimera = costoAsignacion(cursos, aulas, dist, candidatas.head, pesos)
      buscarMejor(candidatas.tail, candidatas.head, costoPrimera)
    }
  }
}