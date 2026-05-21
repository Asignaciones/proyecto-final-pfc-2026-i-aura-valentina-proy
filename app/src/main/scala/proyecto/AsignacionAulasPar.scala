package proyecto

import common._
import proyecto.AsignacionAulas._

object AsignacionAulasPar {

  // =========================================================================
  // 3.1 CHOQUES EN PARALELO
  // =========================================================================

  def choquesPar(cursos: Cursos, a: Asignacion): Int = {
    val n   = cursos.length
    val mid = n / 2

    def contarIntra(desde: Int, hasta: Int): Int =
      (desde until hasta).toVector.flatMap { i =>
        ((i + 1) until hasta).map { j =>
          if (a(i) >= 0 && a(j) >= 0 && a(i) == a(j) &&
            solapan(cursos(i), cursos(j))) 1 else 0
        }
      }.sum


    def contarCruzados: Int =
      (0 until mid).toVector.flatMap { i =>
        (mid until n).map { j =>
          if (a(i) >= 0 && a(j) >= 0 && a(i) == a(j) &&
            solapan(cursos(i), cursos(j))) 1 else 0
        }
      }.sum

    val (izq, der) = parallel(
      contarIntra(0, mid),
      contarIntra(mid, n)
    )
    izq + der + contarCruzados
  }

  // =========================================================================
  // 3.1 DESPERDICIO EN PARALELO
  // =========================================================================

  def desperdicioPar(cursos: Cursos, aulas: Aulas, a: Asignacion): Int = {
    val n   = cursos.length
    val mid = n / 2

    def desperdicioRango(desde: Int, hasta: Int): Int =
      (desde until hasta)
        .filter(i => a(i) >= 0 && capAula(aulas(a(i))) >= estCurso(cursos(i)))
        .map(i => capAula(aulas(a(i))) - estCurso(cursos(i)))
        .sum

    val (izq, der) = parallel(
      desperdicioRango(0, mid),
      desperdicioRango(mid, n)
    )
    izq + der
  }

  // =========================================================================
  // 3.1 MOVILIDAD EN PARALELO
  // =========================================================================

  def movilidadPar(cursos: Cursos, aulas: Aulas, d: Distancias,
                   a: Asignacion): Int = {
    val n   = cursos.length
    val mid = n / 2

    val (mitadIzq, mitadDer) = parallel(
      cursos.indices.slice(0, mid)
        .filter(i => a(i) >= 0)
        .sortBy(i => iniCurso(cursos(i))),
      cursos.indices.slice(mid, n)
        .filter(i => a(i) >= 0)
        .sortBy(i => iniCurso(cursos(i)))
    )

    def merge(l: Seq[Int], r: Seq[Int]): Seq[Int] =
      (l, r) match {
        case (Seq(), _) => r
        case (_, Seq()) => l
        case (lh +: lt, rh +: _)
          if iniCurso(cursos(lh)) <= iniCurso(cursos(rh)) => lh +: merge(lt, r)
        case (_, rh +: rt) => rh +: merge(l, rt)
      }

    val asignados = merge(mitadIzq, mitadDer)

    if (asignados.length < 2) 0
    else
      asignados
        .sliding(2)
        .map(par => d(a(par.head))(a(par(1))))
        .sum
  }

  // =========================================================================
  // 3.2 GENERACION EN PARALELO
  // =========================================================================

  def generarAsignacionesPar(n: Int, m: Int): Vector[Asignacion] = {
    if (n == 0)
      Vector(Vector.empty[Int])
    else {
      val sufijos = generarAsignaciones(n - 1, m)
      val mid     = m / 2

      def expandir(desde: Int, hasta: Int): Vector[Asignacion] =
        (desde until hasta).toVector.flatMap { j =>
          sufijos.map(sufijo => j +: sufijo)
        }

      val (izq, der) = parallel(
        expandir(0, mid),
        expandir(mid, m)
      )
      izq ++ der
    }
  }

  // =========================================================================
  // 3.3 OPTIMA EN PARALELO
  // =========================================================================

  def asignacionOptimaPar(cursos: Cursos, aulas: Aulas, d: Distancias,
                          w: Pesos): (Asignacion, Int) = {
    val n     = cursos.length
    val m     = aulas.length
    val todas = generarAsignacionesPar(n, m)
    val mid   = todas.length / 2

    def minimoRango(desde: Int, hasta: Int): (Asignacion, Int) = {
      val rango = todas
        .slice(desde, hasta)
        .map(a => (a, costoAsignacion(cursos, aulas, d, a, w)))
      if (rango.isEmpty) (Vector.empty[Int], Int.MaxValue)
      else rango.minBy(_._2)
    }

    val (optIzq, optDer) = parallel(
      minimoRango(0, mid),
      minimoRango(mid, todas.length)
    )

    if (optIzq._2 <= optDer._2) optIzq else optDer
  }
}