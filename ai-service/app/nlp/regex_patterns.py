import re

MRP_PATTERN = re.compile(
    r'(?:M\.?R\.?P\.?|MAX(?:IMUM)?\s*RETAIL\s*PRICE|PRICE|RSP|RS\.?|₹)[^0-9\n]{0,20}(?:RS\.?|₹)?\s*(\d+(?:\.\d{1,2})?)',
    re.IGNORECASE
)

NET_QTY_PATTERN = re.compile(
    r'(?:NET\s*(?:QTY|QUANTITY|WT|WEIGHT|VOL|VOLUME)?|CONTENTS?)\s*[:.-]?\s*(\d+(?:\.\d+)?)\s*([a-zA-Z.]+)',
    re.IGNORECASE
)

MANUFACTURER_PATTERN = re.compile(
    r'(?:MFG\s*(?:&|AND)?\s*PKD\s*BY|MANUFACTURED\s*(?:&|AND)?\s*(?:PACKED|PKD)?\s*BY|PACKED\s*BY|PKD\s*BY|MFG\s*BY|MARKETED\s*BY)\s*[:.-]?\s*([^|\n]+)',
    re.IGNORECASE
)

IMPORTER_PATTERN = re.compile(
    r'(?:IMPORTED\s*(?:AND\s*PACKED\s*)?BY|IMPORTER)\s*[:.-]?\s*([^|\n]+)',
    re.IGNORECASE
)

CONSUMER_CARE_PHONE_PATTERN = re.compile(
    r'(?:CARE|HELPLINE|CALL|TEL|PHONE)\s*[:.-]?\s*(\+?91[\s-]?)?(1800[\s-]?\d{3}[\s-]?\d{3,4}|\d{10}|\d{3,5}[\s-]?\d{6,8})',
    re.IGNORECASE
)

EMAIL_PATTERN = re.compile(
    r'([a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+)',
    re.IGNORECASE
)

ORIGIN_PATTERN = re.compile(
    r'(?:COUNTRY\s*OF\s*ORIGIN|MADE\s*IN|ORIGIN)\s*[:.-]?\s*([A-Za-z\s]+)',
    re.IGNORECASE
)

DATE_PATTERN = re.compile(
    r'(?:DATE\s*OF\s*(?:PACKING|MFG|MANUFACTURE)|PACKING\s*DATE|MFG(?:\s*DATE)?|PACKED|PKD|DOM)\s*[:.-]?\s*(\d{1,2}[/-]\d{2,4}|\b(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*\s*\d{2,4})',
    re.IGNORECASE
)
