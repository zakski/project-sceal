Imports System
Imports alice.tuprolog

Namespace VB2ClassLibrary

        Inherits Prolog

        Dim th As String = ""

        Public Sub New()
            Me.setTheory(New Theory(th))
        End Sub

    End Class

End Namespace