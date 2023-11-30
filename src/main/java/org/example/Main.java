package org.example;

import org.example.JSON.Lectura;
import org.example.database.GestionDB;
import org.example.database.SchemeDB;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int opcion = 0;
        Scanner input = new Scanner(System.in);

        //generar conexion Singleton
        Connection connection = GestionDB.getConnection();
        if (connection == null) {
            System.out.println("La conexión ha fallado");
            opcion = 10;
        } else {
            System.out.println("Conexión establecida");
        }


        //Decidir que función hacer a través de un menú
        while(opcion != 10) {
            System.out.println("""
                    ---------MENÚ--------
                     Opciones de la base de datos del almacén:
                    1. Introducir todos los productos
                    2. Mostrar todos los productos
                    3. Introducir empleado
                    4. Mostrar todos los empleados
                    5. Introducir pedido
                    6. Mostrar todos los pedidos
                    7. Mostrar productos menor de x precio
                    8. Introducir productos fav
                    9. Mostrar productos fav
                    10. Salir""");
            opcion = input.nextInt();
            switch(opcion) {
                case 1:
                    insertarProductos(connection);
                    break;
                case 2:
                    mostrarProductos(connection);
                    break;
                case 3:
                    insertarEmpleado(connection);
                    break;
                case 4:
                    mostrarEmpleados(connection);
                    break;
                case 5:
                    insertarPedido(connection);
                    break;
                case 6:
                    mostrarPedidos(connection);
                    break;
                case 7:
                    mostrarProductosCondicion(connection);
                    break;
                case 8:
                    insertarProductosFav(connection);
                    break;
                case 9:
                    mostrarProductosFav(connection);
                    break;
                case 10:
                    System.out.println("Finalizando programa");
                    break;
                default:
                    System.out.println("No existe esa opción");
                    break;

            }
        }
    }

    //Muestra por consola todos los productos con precio mayor a 1000
    private static void mostrarProductosFav(Connection connection) {
        try {
            StringBuilder productosFav = new StringBuilder();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(String.format("SELECT * FROM %s",
                    SchemeDB.TAB_NAME_PRODUCTOS_FAV));
            while(rs.next()){
                int id = rs.getInt(SchemeDB.TAB_ID);
                int idProducto = rs.getInt(SchemeDB.TAB_PRODUCTOS_FAV_COL[0]);
                productosFav.append(String.format("Id: %d | IdProducto: %d\n",
                        id,
                        idProducto
                ));
            }
            System.out.println(productosFav);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Añade productos con un precio mayor a 1000 en la tabla productos_fav
    private static void insertarProductosFav(Connection connection) {
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(String.format("SELECT %s FROM %s WHERE %s > 1000",
                    SchemeDB.TAB_ID,
                    SchemeDB.TAB_NAME_PRODUCTOS,
                    SchemeDB.TAB_PRODUCTOS_COL[3]
            ));
            while(rs.next()) {

                PreparedStatement preparedStatement = connection.prepareStatement(String.format("INSERT INTO %s (%s) VALUE (%d)",
                        SchemeDB.TAB_NAME_PRODUCTOS_FAV,
                        SchemeDB.TAB_PRODUCTOS_FAV_COL[0],
                        rs.getInt(SchemeDB.TAB_ID)
                        ));
                preparedStatement.execute();

            }
            System.out.println("Los productos han sido introducidos");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Muestra los productos con precio menor a el que tu quieras
    private static void mostrarProductosCondicion(Connection connection) {
        try {
            Scanner input = new Scanner(System.in);
            System.out.println("Productos con precio inferior a: ");
            int precioMenor = input.nextInt();
            StringBuilder productosCondicion = new StringBuilder();
            Statement statement = connection.createStatement();

            //Selecciona los productos con la condición
            ResultSet rs = statement.executeQuery(String.format("SELECT * FROM %s WHERE %s < %d",
                    SchemeDB.TAB_NAME_PRODUCTOS,
                    SchemeDB.TAB_PRODUCTOS_COL[3],
                    precioMenor));

            //Muestra en consola
            while(rs.next()){
                int id = rs.getInt(SchemeDB.TAB_ID);
                String nombre = rs.getString(SchemeDB.TAB_PRODUCTOS_COL[0]);
                String descripcion = rs.getString(SchemeDB.TAB_PRODUCTOS_COL[1]);
                int cantidad = rs.getInt(SchemeDB.TAB_PRODUCTOS_COL[2]);
                int precio = rs.getInt(SchemeDB.TAB_PRODUCTOS_COL[3]);
                productosCondicion.append(String.format("Id: %d | Nombre: %s | Descripcion: %s| Cantidad: %d | Precio: %d \n", id,
                        nombre,
                        descripcion,
                        cantidad,
                        precio
                ));
            }
            System.out.println(productosCondicion);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Muestra todos los pedidos que se han realizado
    public static void mostrarPedidos(Connection connection){
        try {
            StringBuilder pedidos = new StringBuilder();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM " + SchemeDB.TAB_NAME_PEDIDOS);
            while(rs.next()){
                int id = rs.getInt(SchemeDB.TAB_ID);
                String descripcion = rs.getString(SchemeDB.TAB_PEDIDOS_COL[0]);
                int precioTotal = rs.getInt(SchemeDB.TAB_PEDIDOS_COL[1]);
                int idProducto = rs.getInt(SchemeDB.TAB_PEDIDOS_COL[2]);
                pedidos.append(String.format("Id: %d | Descripcion: %s | Precio Total: %d | Id Producto: %d\n",
                        id,
                        descripcion,
                        precioTotal,
                        idProducto
                ));
            }
            System.out.println(pedidos);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    //Añade un pedido agragando la id del prodcuto que quieras y su cantidad
    private static void insertarPedido(Connection connection) {
        try {
            Scanner input = new Scanner(System.in);
            System.out.println("Pon el id del producto que quieres: ");
            int id = input.nextInt();
            System.out.println("Pon la cantidad del producto: ");
            int cantidadComprada = input.nextInt();
            Statement statement = connection.createStatement();

            //Calculo el precio total del pedido multiplicando la cantidad con el precio del producto
            ResultSet rs = statement.executeQuery(String.format("SELECT (%s*%d) as precioTotal FROM %s WHERE id = %d",
                    SchemeDB.TAB_PRODUCTOS_COL[3],
                    cantidadComprada,
                    SchemeDB.TAB_NAME_PRODUCTOS,
                    id
                    ));
            rs.next();
            int precioTotal = rs.getInt("precioTotal");
            String descripcion = "Id de producto: " + id + "\nCantidad: " + cantidadComprada;

            //Añadir a la bbdd
            statement.executeUpdate(String.format("INSERT INTO %s (%s,%s,%s) VALUE ('%s',%d,%d)",
                    SchemeDB.TAB_NAME_PEDIDOS,
                    SchemeDB.TAB_PEDIDOS_COL[0],
                    SchemeDB.TAB_PEDIDOS_COL[1],
                    SchemeDB.TAB_PEDIDOS_COL[2],
                    descripcion,precioTotal,id));
            System.out.println("El pedido ha sido introducido");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //Muestra todos los datos de la tabla empleados
    public static void mostrarEmpleados(Connection connection){
        try {
            StringBuilder empleados = new StringBuilder();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM " + SchemeDB.TAB_NAME_EMPLEADOS);
            while(rs.next()){
                int id = rs.getInt(SchemeDB.TAB_ID);
                String nombreEmpleado = rs.getString(SchemeDB.TAB_EMPLEADOS_COL[0]);
                String apellidosEmpleado = rs.getString(SchemeDB.TAB_EMPLEADOS_COL[1]);
                String correoEmpleado = rs.getString(SchemeDB.TAB_EMPLEADOS_COL[2]);
                empleados.append(String.format("Id: %d | Nombre: %s | Apellidos: %s| Correo: %s\n",
                        id,
                        nombreEmpleado,
                        apellidosEmpleado,
                        correoEmpleado
                ));
            }
            System.out.println(empleados);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    //Puedes añadir un empleado con los datos que tu introduzcas
    private static void insertarEmpleado(Connection connection) {

        //Colocar datos del empleado
        Scanner input = new Scanner(System.in);
        System.out.println("Introduce su nombre: ");
        String nombreEmpleado = input.nextLine();
        System.out.println("Introduce sus apellidos: ");
        String apellidosEmpleado = input.nextLine();
        System.out.println("Introduce su correo: ");
        String correoEmpleado = input.nextLine();

        //Añadir a la bbdd
        try {
            Statement statement = connection.createStatement();
            int rows = statement.executeUpdate(String.format("INSERT INTO %s (%s,%s,%s) VALUES ('%s','%s','%s')",
                    SchemeDB.TAB_NAME_EMPLEADOS,
                    SchemeDB.TAB_EMPLEADOS_COL[0],
                    SchemeDB.TAB_EMPLEADOS_COL[1],
                    SchemeDB.TAB_EMPLEADOS_COL[2],
                    nombreEmpleado,apellidosEmpleado,correoEmpleado));
            statement.close();
            System.out.println("El empleado ha sido introducido. Columnas afectadas: " + rows);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Introduce todos los productos del JSON
    public static void insertarProductos(Connection connection){

        Lectura lectura = new Lectura("https://dummyjson.com/products");
        JSONObject response = lectura.convertirJSON(lectura.leerArchivo());
        JSONArray productos = response.getJSONArray("products");

        //Obtener productos del JSON
        for(int i=0;i<productos.length();i++) {
            JSONObject producto = productos.getJSONObject(i);
            String nombre = producto.getString("title");
            String descripcion = producto.getString("description");
            int precio = producto.getInt("price");
            int cantidad = producto.getInt("stock");

            //Añadir a la bbdd

            try {
                PreparedStatement preparedStatement = connection.prepareStatement(String.format("INSERT INTO %s (%s,%s,%s,%s) VALUE (?,?,?,?)",
                        SchemeDB.TAB_NAME_PRODUCTOS,
                        SchemeDB.TAB_PRODUCTOS_COL[0],
                        SchemeDB.TAB_PRODUCTOS_COL[1],
                        SchemeDB.TAB_PRODUCTOS_COL[2],
                        SchemeDB.TAB_PRODUCTOS_COL[3]));
                preparedStatement.setString(1, nombre);
                preparedStatement.setString(2, descripcion);
                preparedStatement.setInt(3, cantidad);
                preparedStatement.setInt(4, precio);
                preparedStatement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Los productos han sido introducidos");
    }

    //Muestra todos los productos de la tabla productos
    public static void mostrarProductos(Connection connection){
        try {
            StringBuilder productos = new StringBuilder();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM " + SchemeDB.TAB_NAME_PRODUCTOS);
            while(rs.next()){
                int id = rs.getInt(SchemeDB.TAB_ID);
                String nombre = rs.getString(SchemeDB.TAB_PRODUCTOS_COL[0]);
                String descripcion = rs.getString(SchemeDB.TAB_PRODUCTOS_COL[1]);
                int cantidad = rs.getInt(SchemeDB.TAB_PRODUCTOS_COL[2]);
                int precio = rs.getInt(SchemeDB.TAB_PRODUCTOS_COL[3]);
                productos.append(String.format("Id: %d | Nombre: %s | Descripcion: %s| Cantidad: %d | Precio: %d \n", id,
                        nombre,
                        descripcion,
                        cantidad,
                        precio
                ));
            }
            System.out.println(productos);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }



}