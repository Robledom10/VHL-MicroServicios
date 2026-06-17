import os


class Settings:

    AUTH_SERVICE_URL = os.getenv("AUTH_SERVICE_URL")

    RESERVATION_SERVICE_URL = os.getenv(
        "RESERVATION_SERVICE_URL"
    )

    TOURIST_CATALOG_SERVICE_URL = os.getenv(
        "TOURIST_CATALOG_SERVICE_URL"
    )

    GALLERY_SERVICE_URL = os.getenv(
        "GALLERY_SERVICE_URL"
    )

    FINANCIAL_SERVICE_URL = os.getenv(
        "FINANCIAL_SERVICE_URL"
    )

    OPERATION_SERVICE_URL = os.getenv(
        "OPERATION_SERVICE_URL"
    )

    DOCUMENT_SERVICE_URL = os.getenv(
        "DOCUMENT_SERVICE_URL"
    )


settings = Settings()

print("AUTH_SERVICE_URL =", settings.AUTH_SERVICE_URL)