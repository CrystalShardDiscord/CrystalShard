package de.kaleidox.crystalshard.internal.items.permission;

import de.kaleidox.crystalshard.main.items.permission.Permission;
import de.kaleidox.crystalshard.main.items.permission.PermissionApplyable;
import de.kaleidox.crystalshard.main.items.permission.PermissionList;

import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PermissionListInternal extends HashSet<Permission> implements PermissionList {
    private final PermissionApplyable parent;

    public PermissionListInternal(PermissionApplyable parent, int permissionInteger) {
        super(Stream.of(Permission.values())
                .filter(permission -> permission.partOf(permissionInteger))
                .filter(permission -> permission != Permission.EMPTY)
                .collect(Collectors.toList()));
        this.parent = parent;
    }

    public PermissionListInternal(PermissionApplyable parent) {
        super();
        this.parent = parent;
    }

    public PermissionListInternal(int permissionInteger) {
        this(null, permissionInteger);
    }

    @Override
    public int toPermissionInt() {
        int value = Permission.EMPTY.getValue();

        forEach(permission -> permission.apply(value, true));

        return value;
    }

    @Override
    public Optional<PermissionApplyable> getParent() {
        return Optional.ofNullable(parent);
    }

    @Override
    public boolean add(Permission permission) {
        boolean success = false;

        if (!contains(permission)) {
            super.add(permission);
        }

        return success;
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof Permission) {
            boolean success = false;

            if (contains(o)) {
                super.remove(o);
            }

            return success;
        } else return false;
    }
}