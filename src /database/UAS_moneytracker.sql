-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 12, 2025 at 05:27 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `moneytracker`
--

-- --------------------------------------------------------

--
-- Table structure for table `category`
--

CREATE TABLE `category` (
  `id` int(11) NOT NULL,
  `category` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `category`
--

INSERT INTO `category` (`id`, `category`) VALUES
(1, 'Work'),
(2, 'Freelance'),
(3, 'Investments'),
(4, 'Groceries'),
(5, 'Transport'),
(6, 'Healthcare'),
(7, 'Subscriptions'),
(8, 'Pet');

-- --------------------------------------------------------

--
-- Table structure for table `transaction`
--

CREATE TABLE `transaction` (
  `id` int(11) NOT NULL,
  `type` varchar(50) NOT NULL,
  `date` date NOT NULL,
  `category` varchar(50) NOT NULL,
  `item` varchar(50) NOT NULL,
  `amount` int(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transaction`
--

INSERT INTO `transaction` (`id`, `type`, `date`, `category`, `item`, `amount`) VALUES
(1, 'Income', '2024-12-25', 'Work', 'Salary', 8000000),
(2, 'Income', '2024-11-25', 'Work', 'Salary', 8000000),
(3, 'Income', '2024-10-25', 'Work', 'Salary', 8000000),
(4, 'Income', '2024-09-25', 'Work', 'Salary', 8000000),
(5, 'Income', '2024-08-25', 'Work', 'Salary', 8000000),
(6, 'Income', '2024-12-10', 'Freelance', 'Graphic Designer', 2000000),
(7, 'Income', '2024-11-09', 'Freelance', 'UI Designer', 4000000),
(8, 'Income', '2024-10-01', 'Freelance', 'Photographer', 3000000),
(9, 'Income', '2024-09-15', 'Freelance', 'Photographer', 3000000),
(10, 'Income', '2024-08-22', 'Freelance', 'Translator', 5000000),
(11, 'Income', '2024-12-12', 'Investments', 'Stock A', 500000),
(12, 'Income', '2024-11-16', 'Investments', 'Stock A', 450000),
(13, 'Income', '2024-10-13', 'Investments', 'Stock A', 300000),
(14, 'Income', '2024-09-15', 'Investments', 'Stock A', 350000),
(15, 'Income', '2024-08-11', 'Investments', 'Stock A', 600000),
(16, 'Expense', '2024-12-05', 'Groceries', 'Food', 50000),
(17, 'Expense', '2024-12-05', 'Groceries', 'Beverages', 20000),
(18, 'Expense', '2024-12-05', 'Groceries', 'Fruits', 50000),
(19, 'Expense', '2024-12-05', 'Groceries', 'Milk', 30000),
(20, 'Expense', '2025-01-10', 'Groceries', 'Ice cream', 15000),
(21, 'Expense', '2025-01-10', 'Transport', 'Transjakarta', 8500),
(22, 'Expense', '2025-01-10', 'Transport', 'Gojek', 25000),
(23, 'Expense', '2025-01-10', 'Transport', 'KRL', 7000),
(24, 'Expense', '2025-01-10', 'Transport', 'MRT', 10000),
(25, 'Expense', '2025-01-10', 'Transport', 'Taxi', 50000),
(26, 'Expense', '2024-12-07', 'Pet', 'Food', 100000),
(27, 'Expense', '2024-12-07', 'Pet', 'Veterinary Care', 200000),
(28, 'Expense', '2025-01-05', 'Pet', 'Grooming', 100000),
(29, 'Expense', '2024-12-01', 'Pet', 'Toys', 25000),
(30, 'Expense', '2024-12-08', 'Pet', 'Supplies', 50000),
(31, 'Expense', '2024-12-05', 'Healthcare', 'Prescriptions', 150000),
(32, 'Expense', '2024-12-20', 'Healthcare', 'Dental care', 100000),
(33, 'Expense', '2024-07-31', 'Healthcare', 'MCU', 500000),
(34, 'Expense', '2024-11-24', 'Healthcare', 'Change glasses', 2000000),
(35, 'Expense', '2025-01-04', 'Healthcare', 'Therapy', 400000),
(36, 'Expense', '2024-12-01', 'Subscriptions', 'Netflix', 54000),
(37, 'Expense', '2024-12-05', 'Subscriptions', 'Apple Music', 79000),
(38, 'Expense', '2024-12-05', 'Subscriptions', 'PlayStation Plus', 169000),
(39, 'Expense', '2024-12-05', 'Subscriptions', 'Disney+', 39000),
(40, 'Expense', '2024-12-05', 'Subscriptions', 'Pool membership', 250000);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `transaction`
--
ALTER TABLE `transaction`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `category`
--
ALTER TABLE `category`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=157;

--
-- AUTO_INCREMENT for table `transaction`
--
ALTER TABLE `transaction`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=61;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
