package ru.alex2772.vcpkggui.model;

import java.util.List;

public class AvailablePackageTableModel extends PackageTableModel {
    public AvailablePackageTableModel(List<VcpkgPackage> packages) {
        super(packages);
    }

    @Override
    protected Object getPlatform(VcpkgPackage item) {
        return item.getSupportedPlatform();
    }
}
