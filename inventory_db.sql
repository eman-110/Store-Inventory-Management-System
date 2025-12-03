CREATE DATABASE IF NOT EXISTS inventory_db;
USE inventory_db;


-- -------------------
-- AuctionReceived
-- -------------------
CREATE TABLE AuctionReceived (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    ItemName VARCHAR(255) NOT NULL,
    Quantity INT NOT NULL,
    LetterNo VARCHAR(100),
    ReceiveDate DATE,
    Remarks VARCHAR(500)
);

-- -------------------
-- StoreReceived
-- -------------------
CREATE TABLE StoreReceived (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    ItemName VARCHAR(255) NOT NULL,
    Quantity INT NOT NULL,
    ReceivedFrom VARCHAR(100),
    ReceiveDate DATE,
    UnitPrice INT,
    Remarks VARCHAR(500)
);

-- -------------------
-- Other Tables
-- -------------------
CREATE TABLE MinuteSheet (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    PurchaseOrderNo VARCHAR(255) NOT NULL,
    Vendor VARCHAR(255) NOT NULL,
    StoreReceivedID INT,
    FOREIGN KEY (StoreReceivedID) REFERENCES StoreReceived(ID)
);

CREATE TABLE PettyCash (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    VoucherNo VARCHAR(255) NOT NULL,
    StoreReceivedID INT,
    FOREIGN KEY (StoreReceivedID) REFERENCES StoreReceived(ID)
);

CREATE TABLE Replacement (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    Branch VARCHAR(255) NOT NULL,
    StoreReceivedID INT,
    FOREIGN KEY (StoreReceivedID) REFERENCES StoreReceived(ID)
);

CREATE TABLE DHAEmployee (
    DHAID INT AUTO_INCREMENT PRIMARY KEY,
    EmpName VARCHAR(255) NOT NULL,
    Department VARCHAR(255) NOT NULL,
    Designation VARCHAR(255) NOT NULL,
    Scale INT
);

CREATE TABLE UserStatus (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    StatusName VARCHAR(255) NOT NULL
);

CREATE TABLE UserReceived (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    ItemName VARCHAR(255) NOT NULL,
    Quantity INT NOT NULL,
    PurchaseOrderNo VARCHAR(255) NOT NULL,
    Vendor VARCHAR(255) NOT NULL,
    ReceiveDate DATE,
    DHAID INT,
    WarrentyDemand VARCHAR(255) NOT NULL,
    WarrentyProvided VARCHAR(255) NOT NULL,
    StatusID INT,
    Remarks VARCHAR(500),

    FOREIGN KEY (DHAID) REFERENCES DHAEmployee(DHAID),
    FOREIGN KEY (StatusID) REFERENCES UserStatus(ID)
);

CREATE TABLE Issued (
    UserID INT,
    IssuedDate DATE,
    FOREIGN KEY (UserID) REFERENCES UserReceived(ID)
);

CREATE TABLE Pending (
    UserID INT,
    Reason VARCHAR(255) NOT NULL,
    FOREIGN KEY (UserID) REFERENCES UserReceived(ID)
);

CREATE TABLE AuctionIssued (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    AuctionID INT,
    Quantity INT NOT NULL,
    IssuedDate DATE,
    Remarks VARCHAR(255) NOT NULL,
    FOREIGN KEY (AuctionID) REFERENCES AuctionReceived(ID)
);

CREATE TABLE UserIssued (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    IssuedDate DATE,
    Remarks VARCHAR(255) NOT NULL,
    FOREIGN KEY (UserID) REFERENCES UserReceived(ID)
);

CREATE TABLE Branch (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    BranchName VARCHAR(255) NOT NULL
);

CREATE TABLE IssueType (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    TypeName VARCHAR(255) NOT NULL
);

CREATE TABLE StoreIssued (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    StoreRID INT NOT NULL,
    Quantity INT NOT NULL,
    BranchID INT NOT NULL,
    IssuedDate DATE,
    IssueTypeID INT,
    RegisterNo VARCHAR(255) NOT NULL,
    Remarks VARCHAR(255) NOT NULL,

    FOREIGN KEY (StoreRID) REFERENCES StoreReceived(ID),
    FOREIGN KEY (BranchID) REFERENCES Branch(ID),
    FOREIGN KEY (IssueTypeID) REFERENCES IssueType(ID)
);

CREATE TABLE Reture (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    StoreIssueID INT NOT NULL,
    Quantity INT NOT NULL,
    ReturnDate DATE,

    FOREIGN KEY (StoreIssueID) REFERENCES StoreIssued(ID)
);

CREATE TABLE InStore (
    ItemName VARCHAR(255) PRIMARY KEY,
    RemainingQty INT NOT NULL
);

CREATE TABLE LowStock (
    InStoreName VARCHAR(255) PRIMARY KEY,
    LowStockLimit INT NOT NULL DEFAULT 0,
    FOREIGN KEY (InStoreName) REFERENCES InStore(ItemName)
);
-- -------------------
-- Insert Fixes
-- -------------------
INSERT INTO IssueType (ID, TypeName)
VALUES
(1, 'Temporary'),
(2, 'Permanent');

INSERT INTO UserStatus (ID, StatusName)
VALUES
(1, 'Issued'),
(2, 'Pending');



-- -------------------
-- Remaining View FIXED
-- -------------------
CREATE VIEW RemainingView AS
SELECT 
    si.ID AS StoreIssueID,
    si.StoreRID,
    si.Quantity AS IssuedQuantity,
    it.TypeName AS IssueType,
    IFNULL(SUM(r.Quantity), 0) AS ReturnedQuantity,
    (si.Quantity - IFNULL(SUM(r.Quantity), 0)) AS RemainingQuantity
FROM StoreIssued si
LEFT JOIN IssueType it ON si.IssueTypeID = it.ID
LEFT JOIN Reture r ON si.ID = r.StoreIssueID
WHERE si.IssueTypeID = 1
GROUP BY si.ID, si.StoreRID, si.Quantity, it.TypeName;


CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `username` varchar(255) NOT NULL,
                        `password` varchar(255) NOT NULL,
                        `role` enum('ADMIN','USER','MODERATOR') NOT NULL DEFAULT 'USER',
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `user` (`username`, `password`, `role`) VALUES
('admin', 'admin123', 'ADMIN')


DELIMITER $$

CREATE TRIGGER trg_update_instore
AFTER INSERT ON StoreIssued
FOR EACH ROW
BEGIN
    DECLARE itemNameVal VARCHAR(255);
    DECLARE totalReceived INT DEFAULT 0;
    DECLARE totalIssued INT DEFAULT 0;

    -- Get the ItemName from StoreReceived
    SELECT ItemName INTO itemNameVal
    FROM StoreReceived
    WHERE ID = NEW.StoreRID;

    -- Total received for this ItemName
    SELECT SUM(Quantity) INTO totalReceived
    FROM StoreReceived
    WHERE ItemName = itemNameVal;

    -- Total issued for this ItemName
    SELECT SUM(si.Quantity) INTO totalIssued
    FROM StoreIssued si
    JOIN StoreReceived sr ON si.StoreRID = sr.ID
    WHERE sr.ItemName = itemNameVal;

    -- Insert/Update remaining quantity for this ItemName
    INSERT INTO InStore (StoreReceivedID, RemainingQty)
    VALUES (NEW.StoreRID, totalReceived - totalIssued)
    ON DUPLICATE KEY UPDATE RemainingQty = totalReceived - totalIssued;

END$$

DELIMITER ;

SELECT * From User;