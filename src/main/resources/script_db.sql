-- ELIMINAR LA BASE DE DATOS SI EXISTE Y CREARLA DE NUEVO
DROP DATABASE IF EXISTS desarrolloweb;
CREATE DATABASE desarrolloweb;
USE desarrolloweb;

-- 1. TABLA ROLES
CREATE TABLE roles (
    id_rol INT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

INSERT INTO roles (id_rol, nombre) VALUES (1, 'CLIENTE');
INSERT INTO roles (id_rol, nombre) VALUES (2, 'ADMINISTRADOR');
INSERT INTO roles (id_rol, nombre) VALUES (3, 'REPARTIDOR');
INSERT INTO roles (id_rol, nombre) VALUES (4, 'VENDEDOR');

-- 2. TABLA USUARIOS (Fusión de Cliente y Usuario)
CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre_completo VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    id_rol INT NOT NULL,
    tipo_documento VARCHAR(20),
    numero_documento VARCHAR(20) UNIQUE,
    telefono VARCHAR(20),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
);

-- 3. TABLA DIRECCIONES
CREATE TABLE direcciones (
    id_direccion INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    direccion_exacta VARCHAR(255) NOT NULL,
    referencia VARCHAR(255),
    distrito VARCHAR(100) NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- 4. TABLA PRODUCTOS
CREATE TABLE productos (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    marca VARCHAR(100) NOT NULL,
    talla VARCHAR(20) NOT NULL,
    color VARCHAR(50) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL,
    imagen VARCHAR(500),
    descripcion TEXT
);

-- 5. TABLA PEDIDOS
CREATE TABLE pedidos (
    id_pedido INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_repartidor INT,
    id_direccion INT NOT NULL,
    estado VARCHAR(50) NOT NULL,
    total_pagar DECIMAL(10,2) NOT NULL,
    fecha_pedido DATETIME NOT NULL,
    tipo_pedido VARCHAR(20) NOT NULL DEFAULT 'WEB',
    FOREIGN KEY (id_cliente) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_direccion) REFERENCES direcciones(id_direccion)
);

-- 6. TABLA DETALLE_PEDIDOS
CREATE TABLE detalle_pedidos (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);

-- 7. TABLA PAGOS
CREATE TABLE pagos (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL,
    codigo_transaccion VARCHAR(100),
    estado_pago VARCHAR(50) NOT NULL,
    FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido) ON DELETE CASCADE
);

-- 8. TABLA COMPROBANTES
CREATE TABLE comprobantes (
    id_comprobante INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    tipo_comprobante VARCHAR(50) NOT NULL,
    serie_numero VARCHAR(100) NOT NULL UNIQUE,
    fecha_emision DATETIME NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    igv DECIMAL(10,2) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido) ON DELETE CASCADE
);

-- 9. TABLA CAMPANAS PROMOCIONALES
CREATE TABLE campanas_promocionales (
    id_campana INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    porcentaje_descuento DECIMAL(5,2) NOT NULL,
    filtro_marca VARCHAR(100),
    filtro_color VARCHAR(100),
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT TRUE
);

-- ==========================================
-- DATOS DE PRUEBA: USUARIOS PARA CADA ROL
-- (Contraseña para todos: password123)
-- ==========================================
INSERT INTO usuarios (nombre_completo, email, password, id_rol, activo) VALUES 
('Juan Cliente', 'cliente@gmail.com', '$2a$10$YmgqYTAqXsc.i1CtdkbSi.QW.sS7pmAI98YEpBQPlFnweJMjht/pu', 1, TRUE),
('Ana Administradora', 'administrador@gmail.com', '$2a$10$YmgqYTAqXsc.i1CtdkbSi.QW.sS7pmAI98YEpBQPlFnweJMjht/pu', 2, TRUE),
('Carlos Repartidor', 'repartidor@gmail.com', '$2a$10$YmgqYTAqXsc.i1CtdkbSi.QW.sS7pmAI98YEpBQPlFnweJMjht/pu', 3, TRUE),
('Maria Vendedora', 'vendedor@gmail.com', '$2a$10$YmgqYTAqXsc.i1CtdkbSi.QW.sS7pmAI98YEpBQPlFnweJMjht/pu', 4, TRUE);

-- ==========================================
-- DATOS DE PRUEBA: DIRECCION PARA EL CLIENTE
-- ==========================================
INSERT INTO direcciones (id_usuario, direccion_exacta, referencia, distrito) VALUES
(1, 'Av. Los Olivos 123', 'Frente al parque', 'Los Olivos');

-- ==========================================
-- DATOS DE PRUEBA: PRODUCTO (ZAPATO) POR DEFECTO
-- ==========================================
INSERT INTO productos (id_producto, nombre, categoria, marca, talla, color, precio, stock, imagen, descripcion) VALUES
(1, 'Air Max 90', 'Deportivo', 'Nike', '42', 'Blanco', 350.00, 10, 'nike_air_max.jpg', 'Zapatillas deportivas cómodas para correr.'),
(2, 'Classic Leather', 'Casual', 'Reebok', '40', 'Rojo', 250.00, 15, 'reebok_classic.jpg', 'Zapatillas clásicas de cuero.');

-- ==========================================
-- DATOS DE PRUEBA: CAMPANAS PROMOCIONALES
-- ==========================================
-- Fiestas Patrias: 20% descuento a zapatos Blancos o Rojos, todo julio.
INSERT INTO campanas_promocionales (nombre, porcentaje_descuento, filtro_marca, filtro_color, fecha_inicio, fecha_fin, activa) VALUES
('Fiestas Patrias Blanco', 20.00, NULL, 'Blanco', '2026-07-01', '2026-07-31', TRUE),
('Fiestas Patrias Rojo', 20.00, NULL, 'Rojo', '2026-07-01', '2026-07-31', TRUE),
('Sale Nike Todo el Mes', 10.00, 'Nike', NULL, '2026-07-01', '2026-07-31', TRUE);
