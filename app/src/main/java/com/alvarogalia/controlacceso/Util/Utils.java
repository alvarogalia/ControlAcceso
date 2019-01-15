package com.alvarogalia.controlacceso.Util;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;

public class Utils {

    public static String validarRut(String rut) {
        String validacion = "Rut no coincide con dígito verificador";
        try {
            rut =  rut.toUpperCase();
            rut = rut.replace(".", "");
            rut = rut.replace("-", "");
            int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));

            char dv = rut.charAt(rut.length() - 1);

            int m = 0, s = 1;
            for (; rutAux != 0; rutAux /= 10) {
                s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
            }
            if (dv == (char) (s != 0 ? s + 47 : 75)) {
                validacion = "OK";
            }

        } catch (java.lang.NumberFormatException e) {
            validacion = "Error en formato RUT";
        } catch (Exception e) {
            validacion = "Error desconocido validando RUT";
        }
        return validacion;
    }

    public static void changeBackgroundColor(View vista, String newColor){
        vista.getBackground().setColorFilter(Color.parseColor(newColor),PorterDuff.Mode.MULTIPLY);
    }

    public static String leftPad(String originalString, int length){
        StringBuilder sb = new StringBuilder();
        if(originalString.length() > length){
            originalString = originalString.substring(0, length);
        }
        while (sb.length() + originalString.length() < length) {
            sb.append(' ');
        }
        sb.append(originalString);
        String paddedString = sb.toString();
        return paddedString;
    }
    public static String rightPad(String originalString, int length){
        StringBuilder sb = new StringBuilder();
        String trimed;
        if(originalString.length() > length){
            trimed = originalString.substring(0, length);
        }else{
            trimed = originalString;
        }
        sb.append(trimed);
        while (sb.length() < length) {
            sb.append(' ');
        }

        String paddedString = sb.toString();
        return paddedString;
    }
}
