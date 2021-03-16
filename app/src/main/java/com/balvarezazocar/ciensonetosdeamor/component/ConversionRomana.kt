package com.balvarezazocar.ciensonetosdeamor.component

fun convertirANumerosRomanos(numero: Int): String? {
    var i: Int
    val miles: Int
    val centenas: Int
    val decenas: Int
    val unidades: Int
    var romano = ""
    //obtenemos cada cifra del número
    miles = numero / 1000
    centenas = numero / 100 % 10
    decenas = numero / 10 % 10
    unidades = numero % 10

    //millar
    i = 1
    while (i <= miles) {
        romano = romano + "M"
        i++
    }

    //centenas
    if (centenas == 9) {
        romano = romano + "CM"
    } else if (centenas >= 5) {
        romano = romano + "D"
        i = 6
        while (i <= centenas) {
            romano = romano + "C"
            i++
        }
    } else if (centenas == 4) {
        romano = romano + "CD"
    } else {
        i = 1
        while (i <= centenas) {
            romano = romano + "C"
            i++
        }
    }

    //decenas
    if (decenas == 9) {
        romano = romano + "XC"
    } else if (decenas >= 5) {
        romano = romano + "L"
        i = 6
        while (i <= decenas) {
            romano = romano + "X"
            i++
        }
    } else if (decenas == 4) {
        romano = romano + "XL"
    } else {
        i = 1
        while (i <= decenas) {
            romano = romano + "X"
            i++
        }
    }

    //unidades
    if (unidades == 9) {
        romano = romano + "IX"
    } else if (unidades >= 5) {
        romano = romano + "V"
        i = 6
        while (i <= unidades) {
            romano = romano + "I"
            i++
        }
    } else if (unidades == 4) {
        romano = romano + "IV"
    } else {
        i = 1
        while (i <= unidades) {
            romano = romano + "I"
            i++
        }
    }
    return romano
}