-- phpMyAdmin SQL Dump
-- version 4.6.4
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 23, 2019 at 01:19 AM
-- Server version: 5.7.14
-- PHP Version: 5.6.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `koprasidb`
--

-- --------------------------------------------------------

--
-- Table structure for table `anggota`
--

CREATE TABLE `anggota` (
  `no_anggota` varchar(4) NOT NULL,
  `nama` varchar(30) DEFAULT NULL,
  `tgl_masuk` date DEFAULT NULL,
  `handphone` varchar(15) DEFAULT NULL,
  `rt` varchar(2) DEFAULT NULL,
  `saldo_pokok` bigint(20) DEFAULT NULL,
  `saldo_wajib` bigint(20) DEFAULT NULL,
  `saldo_sukarela` bigint(20) DEFAULT NULL,
  `aktif` enum('0','1') NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `anggota`
--

INSERT INTO `anggota` (`no_anggota`, `nama`, `tgl_masuk`, `handphone`, `rt`, `saldo_pokok`, `saldo_wajib`, `saldo_sukarela`, `aktif`) VALUES
('1', 'DZIKRY', '2017-01-04', '08987066023', '06', 35000, 60000, 150000, '1');

-- --------------------------------------------------------

--
-- Table structure for table `angsuran`
--

CREATE TABLE `angsuran` (
  `no_angsuran` varchar(15) NOT NULL,
  `tgl_bayar` date DEFAULT NULL,
  `no_pinjaman` varchar(15) DEFAULT NULL,
  `aktif` enum('0','1') NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `angsuran`
--

INSERT INTO `angsuran` (`no_angsuran`, `tgl_bayar`, `no_pinjaman`, `aktif`) VALUES
('1', '2017-01-17', '109052017', '1');

-- --------------------------------------------------------

--
-- Table structure for table `login`
--

CREATE TABLE `login` (
  `username` varchar(15) NOT NULL,
  `password` varchar(15) NOT NULL,
  `no_anggota` varchar(4) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `login`
--

INSERT INTO `login` (`username`, `password`, `no_anggota`) VALUES
('ADMIN', 'ADMIN', '1');

-- --------------------------------------------------------

--
-- Table structure for table `pinjaman`
--

CREATE TABLE `pinjaman` (
  `no_pinjaman` varchar(15) NOT NULL,
  `tgl_pinjaman` date DEFAULT NULL,
  `jumlah_pinjaman` bigint(20) DEFAULT NULL,
  `bunga` bigint(20) DEFAULT NULL,
  `total_pinjaman` bigint(20) DEFAULT NULL,
  `lama_pinjaman` int(2) DEFAULT NULL,
  `angsuran` bigint(20) DEFAULT NULL,
  `sisa_pinjaman` bigint(20) DEFAULT NULL,
  `no_anggota` varchar(4) NOT NULL,
  `status_pinjaman` enum('BELUM LUNAS','SUDAH LUNAS') NOT NULL DEFAULT 'BELUM LUNAS',
  `aktif` enum('0','1') NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `pinjaman`
--

INSERT INTO `pinjaman` (`no_pinjaman`, `tgl_pinjaman`, `jumlah_pinjaman`, `bunga`, `total_pinjaman`, `lama_pinjaman`, `angsuran`, `sisa_pinjaman`, `no_anggota`, `status_pinjaman`, `aktif`) VALUES
('109052017', '2017-05-09', 100000, 5000, 105000, 2, 35000, 70000, '1', 'BELUM LUNAS', '1');

-- --------------------------------------------------------

--
-- Table structure for table `simpanan`
--

CREATE TABLE `simpanan` (
  `no_simpanan` varchar(15) NOT NULL,
  `tgl_simpanan` date DEFAULT NULL,
  `jenis_simpanan` varchar(10) DEFAULT NULL,
  `jumlah_simpanan` bigint(20) DEFAULT NULL,
  `no_anggota` varchar(4) NOT NULL,
  `aktif` enum('0','1') NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `simpanan`
--

INSERT INTO `simpanan` (`no_simpanan`, `tgl_simpanan`, `jenis_simpanan`, `jumlah_simpanan`, `no_anggota`, `aktif`) VALUES
('SP104012017', '2017-01-04', 'POKOK', 35000, '1', '1'),
('SS110012017', '2017-01-10', 'SUKARELA', 100000, '1', '1'),
('SS127012017', '2017-01-27', 'SUKARELA', 50000, '1', '1'),
('SW104042017', '2017-04-04', 'WAJIB', 15000, '1', '0'),
('SW107022017', '2017-02-07', 'WAJIB', 15000, '1', '1'),
('SW107032017', '2017-03-07', 'WAJIB', 15000, '1', '1'),
('SW109052017', '2017-05-09', 'WAJIB', 15000, '1', '1'),
('SW112012017', '2017-01-12', 'WAJIB', 15000, '1', '1');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `anggota`
--
ALTER TABLE `anggota`
  ADD PRIMARY KEY (`no_anggota`);

--
-- Indexes for table `angsuran`
--
ALTER TABLE `angsuran`
  ADD PRIMARY KEY (`no_angsuran`),
  ADD KEY `pembayaran` (`no_pinjaman`);

--
-- Indexes for table `login`
--
ALTER TABLE `login`
  ADD PRIMARY KEY (`username`,`password`),
  ADD KEY `no_anggota` (`no_anggota`);

--
-- Indexes for table `pinjaman`
--
ALTER TABLE `pinjaman`
  ADD PRIMARY KEY (`no_pinjaman`),
  ADD KEY `memiliki_pinjaman` (`no_anggota`);

--
-- Indexes for table `simpanan`
--
ALTER TABLE `simpanan`
  ADD PRIMARY KEY (`no_simpanan`),
  ADD KEY `memiliki_simpanan` (`no_anggota`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `angsuran`
--
ALTER TABLE `angsuran`
  ADD CONSTRAINT `pembayaran` FOREIGN KEY (`no_pinjaman`) REFERENCES `pinjaman` (`no_pinjaman`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `login`
--
ALTER TABLE `login`
  ADD CONSTRAINT `login_ibfk_1` FOREIGN KEY (`no_anggota`) REFERENCES `anggota` (`no_anggota`);

--
-- Constraints for table `pinjaman`
--
ALTER TABLE `pinjaman`
  ADD CONSTRAINT `memiliki_pinjaman` FOREIGN KEY (`no_anggota`) REFERENCES `anggota` (`no_anggota`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `simpanan`
--
ALTER TABLE `simpanan`
  ADD CONSTRAINT `memiliki_simpanan` FOREIGN KEY (`no_anggota`) REFERENCES `anggota` (`no_anggota`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
