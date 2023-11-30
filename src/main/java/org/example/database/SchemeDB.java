package org.example.database;

public interface SchemeDB {
    String HOST = "127.0.0.1:3308";
    String DB_NAME = "almacen";
    String TAB_NAME_PRODUCTOS = "productos";
    String TAB_NAME_PEDIDOS = "pedidos";
    String TAB_NAME_EMPLEADOS = "empleados";
    String TAB_NAME_PRODUCTOS_FAV = "productos_fav";
    String TAB_ID = "id";
    String TAB_PRODUCTOS_COL[] = {"nombre", "descripcion", "cantidad", "precio"};
    String TAB_EMPLEADOS_COL[] = {"nombre", "apellidos", "correo"};
    String TAB_PEDIDOS_COL[] = {"descripcion", "precio_total", "idProducto"};
    String TAB_PRODUCTOS_FAV_COL[] = {"idProducto"};
}
