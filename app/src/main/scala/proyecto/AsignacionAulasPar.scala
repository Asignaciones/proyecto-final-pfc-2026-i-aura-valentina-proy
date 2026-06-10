package proyecto

import common._
import AsignacionAulas._
object AsignacionAulasPar {

  // ----------------------------------------------------------
  // 3.1  choquesPar
  // ----------------------------------------------------------
  def choquesPar(cursos: Cursos, asig: Asignacion): Int = {

    def secuencial(desde: Int, hasta: Int): Int = {

      def contar(i: Int, j: Int): Int = {

        if (i >= hasta) 0

        else if (j >= hasta)
          contar(i + 1, i + 2)

        else {

          val choque =
            asig(i) >= 0 &&
              asig(j) >= 0 &&
              asig(i) == asig(j) &&
              AsignacionAulas.solapan(cursos(i), cursos(j))

          (if (choque) 1 else 0) +
            contar(i, j + 1)
        }
      }

      contar(desde, desde + 1)
    }

    def aux(desde: Int, hasta: Int): Int = {

      val tam = hasta - desde

      if (tam <= 2)
        secuencial(desde, hasta)

      else {

        val mitad = (desde + hasta) / 2

        val (izq, der) =
          parallel(
            aux(desde, mitad),
            aux(mitad, hasta)
          )

        val cruzados =
          (for {
            i <- desde until mitad
            j <- mitad until hasta
            if asig(i) >= 0
            if asig(j) >= 0
            if asig(i) == asig(j)
            if AsignacionAulas.solapan(cursos(i), cursos(j))
          } yield 1).sum

        izq + der + cruzados
      }
    }

    aux(0, cursos.length)
  }

  // ----------------------------------------------------------
  // 3.1  desperdicioPar
  // ----------------------------------------------------------
  def desperdicioPar(cursos: Cursos, aulas: Aulas, asig: Asignacion): Int = {

    def desperdicioEnRango(desde: Int, hasta: Int): Int = {
      val tamano = hasta - desde

      if (tamano <= 0) 0
      else if (tamano == 1) {
        if (asig(desde) < 0) 0
        else {
          val capacidad   = AsignacionAulas.capAula(aulas(asig(desde)))
          val estudiantes = AsignacionAulas.estCurso(cursos(desde))
          val sobra       = capacidad - estudiantes
          if (sobra > 0) sobra else 0
        }
      } else {
        val mitad    = (desde + hasta) / 2
        val tareaIzq = task { desperdicioEnRango(desde, mitad) }
        val tareaDer = task { desperdicioEnRango(mitad, hasta) }
        tareaIzq.join() + tareaDer.join()
      }
    }

    desperdicioEnRango(0, cursos.length)
  }

  // ----------------------------------------------------------
  // 3.1  movilidadPar
  // ----------------------------------------------------------
  def movilidadPar(cursos: Cursos, aulas: Aulas, dist: Distancias, asig: Asignacion): Int = {
    val soloAsignados = cursos.indices.filter(asig(_) >= 0).toVector
    val porHorario    = soloAsignados.sortBy((i: Int) => AsignacionAulas.iniCurso(cursos(i)))

    if (porHorario.length <= 1) 0
    else {
      def sumarDistancias(desde: Int, hasta: Int): Int = {
        val tamano = hasta - desde

        if (tamano <= 1) 0
        else if (tamano == 2) {
          dist(asig(porHorario(desde)))(asig(porHorario(desde + 1)))
        } else {
          val mitad      = (desde + hasta) / 2
          val tareaIzq   = task { sumarDistancias(desde, mitad) }
          val tareaDer   = task { sumarDistancias(mitad, hasta) }
          val distEnlace = dist(asig(porHorario(mitad - 1)))(asig(porHorario(mitad)))
          tareaIzq.join() + tareaDer.join() + distEnlace
        }
      }

      sumarDistancias(0, porHorario.length)
    }
  }

  // ----------------------------------------------------------
  // 3.2  generarAsignacionesPar
  // ----------------------------------------------------------
  def generarAsignacionesPar(n: Int, m: Int): Vector[Asignacion] = {

    if (n == 0) {
      Vector(Vector.empty[Int])
    }
    else {

      val subAsignaciones =
        generarAsignacionesPar(n - 1, m)

      def construir(desde: Int, hasta: Int): Vector[Asignacion] = {

        if (hasta - desde <= 1) {
          subAsignaciones.map(sub => desde +: sub)
        }
        else {

          val mitad = (desde + hasta) / 2

          val (izq, der) =
            parallel(
              construir(desde, mitad),
              construir(mitad, hasta)
            )

          izq ++ der
        }
      }

      construir(0, m)
    }
  }

  // ----------------------------------------------------------
  // 3.3  asignacionOptimaPar
  // ----------------------------------------------------------
  def asignacionOptimaPar(cursos: Cursos, aulas: Aulas, dist: Distancias,
                          pesos: Pesos): (Asignacion, Int) = {
    val todasLasCandidatas = generarAsignacionesPar(cursos.length, aulas.length)

    if (todasLasCandidatas.isEmpty) (Vector.empty[Int], 0)
    else {
      def minimoEnRango(
                         desde: Int,
                         hasta: Int
                       ): (Asignacion, Int) = {

        if (desde >= hasta)
          (Vector.empty[Int], Int.MaxValue)

        else if (hasta - desde == 1) {

          val costo =
            AsignacionAulas.costoAsignacion(
              cursos,
              aulas,
              dist,
              todasLasCandidatas(desde),
              pesos
            )

          (todasLasCandidatas(desde), costo)

        } else {

          val mitad = (desde + hasta) / 2

          val (izq, der) =
            parallel(
              minimoEnRango(desde, mitad),
              minimoEnRango(mitad, hasta)
            )

          if (izq._2 <= der._2)
            izq
          else
            der
        }
      }

      minimoEnRango(0, todasLasCandidatas.length)
    }
  }
}