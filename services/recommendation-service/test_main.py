from main import health_check, mock_recommendations


def test_health_check_reports_healthy():
    assert health_check() == {"status": "healthy"}


def test_mock_recommendations_respects_limit():
    assert len(mock_recommendations("any-id", 1)) == 1


def test_mock_recommendations_caps_at_available_items():
    results = mock_recommendations("any-id", 10)
    assert len(results) == 2


def test_mock_recommendations_include_pricing_fields():
    for item in mock_recommendations("any-id", 5):
        assert {"id", "name", "category", "price"} <= item.keys()
